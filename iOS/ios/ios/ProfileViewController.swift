//
//  ProfileViewController.swift
//  ios
//
//  Created by Wanyea on 9/7/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class ProfileViewController: UIViewController, UITableViewDelegate {
    
    var user: User!
    @IBOutlet weak var exitIndicator: UIView!
    
    @IBOutlet weak var nicknameLabel: UILabel!
    @IBOutlet weak var birthdayLabel: UILabel!
    @IBOutlet weak var birthdayIcon: UILabel!
    @IBOutlet weak var mantraLabel: UILabel!
    @IBOutlet weak var noAdventoursMessage: UILabel!
    @IBOutlet weak var noBeaconsMessage: UILabel!
    
    @IBOutlet weak var prevAdventoursTable: UITableView!
    @IBOutlet weak var beaconsTable: UITableView!
    @IBOutlet weak var activityIndicatorInfo: UIActivityIndicatorView!
    @IBOutlet weak var activityIndicatorAdventours: UIActivityIndicatorView!
    @IBOutlet weak var activityIndicatorBeacons: UIActivityIndicatorView!
    
    var prevAdventours: [[String: Any]] = []
    var beacons: [[String: Any]] = []
    
    var prevAdventoursSource: UITableViewDataSource!
    var beaconsSource: UITableViewDataSource!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        self.prevAdventoursTable?.delegate = self
        self.beaconsTable?.delegate = self

    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.hideUserInfo()
        self.prevAdventoursTable?.isHidden = true
        self.beaconsTable?.isHidden = true
        self.activityIndicatorInfo?.startAnimating()
        self.activityIndicatorAdventours?.startAnimating()
        self.activityIndicatorBeacons?.startAnimating()
        self.exitIndicator?.layer.cornerRadius = 3
        getUserData()
        getPrevAdventourData()
        
        

        getBeaconsData()
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if tableView is AdventourTableView {
            let storyboard = UIStoryboard(name: "AdventourSummaryPage", bundle: nil)

            if let locations = prevAdventours[indexPath.item]["locations"] as? [[String: Any]] {
                print("SUCCESSFULLY RETRIEVED LOCATIONS")
                if let vc = storyboard.instantiateViewController(identifier: "AdventourSummary") as? AdventourSummaryViewController {
                    vc.source = self
                    vc.locations = locations
                    self.navigationController?.pushViewController(vc, animated: true)
                }
                
            }
        } else if tableView is BeaconTableView {
            let storyboard = UIStoryboard(name: "BeaconPost", bundle: nil)
            print(beacons[indexPath.item])
            if let locations = beacons[indexPath.item]["locations"] as? [[String: Any]] {
                print("SUCCESSFULLY RETRIEVED LOCATIONS")
                if let vc = storyboard.instantiateViewController(identifier: "BeaconPost") as? BeaconPostViewController {
                    vc.locations = locations
                    vc.source = self
                    vc.beaconInfo = self.beacons[indexPath.item]
                    print("nickname: ", self.nicknameLabel.text)
                    vc.nickname = self.nicknameLabel.text!
                    print("vc nickname: ", vc.nickname)
                    self.navigationController?.pushViewController(vc, animated: true)
                }
                
            }
        }
    }
    
    func getUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
//                    print(user)
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "MM/dd/YYYY"
                    if let firTimestamp = user["birthday"] as? Timestamp {
                        let swiftDate = firTimestamp.dateValue()
                        let stringDate = dateFormatter.string(from: swiftDate)
                        self.birthdayLabel.text = stringDate
                    }
                    if let username = user["nickname"] as? String {
//                        print(username)
                        self.nicknameLabel?.text = username
                    }
                    if let mantra = user["mantra"] as? String {
//                        print(mantra)
                        self.mantraLabel?.text = mantra
                    }
                    DispatchQueue.main.async {
                        self.activityIndicatorInfo?.stopAnimating()
                        self.showUserInfo()
                    }
                    
                }
            }
        }
        
    }
    
    func hideUserInfo() {
        self.nicknameLabel?.isHidden = true
        self.birthdayLabel?.isHidden = true
        self.mantraLabel?.isHidden = true
        self.birthdayIcon?.isHidden = true
    }
    
    func showUserInfo() {
        self.nicknameLabel?.isHidden = false
        self.birthdayLabel?.isHidden = false
        self.mantraLabel?.isHidden = false
        self.birthdayIcon?.isHidden = false
    }
    
    func getPrevAdventourData() {
        
        var allData: [String: Any]!
        self.prevAdventours = []
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        db.collection("Adventourists")
            .document(self.user.uid)
            .collection("adventours")
            .order(by: "dateCreated", descending: true)
            .limit(to: 5)
            .getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                    self.activityIndicatorAdventours?.stopAnimating()
                    self.prevAdventoursTable?.isHidden = false
                    self.noAdventoursMessage?.isHidden = false
                } else if snap!.isEmpty {
                    self.activityIndicatorAdventours?.stopAnimating()
                    self.prevAdventoursTable?.isHidden = false
                    self.noAdventoursMessage?.isHidden = false
                } else {
                    for doc in snap!.documents {
                        let documentID = doc.documentID
                        if let data = doc.data() as? [String: Any] {
                            allData = data
                            allData.updateValue(documentID, forKey: "documentID")
                            if let locations = data["locations"] as? [String] {
                                let params: [String: Any] = [
                                    "uid": self.user!.uid,
                                    "ids": locations
                                ]
                                
                                let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-foursquare-places")
                                var urlRequest = URLRequest(url: url!)
                                urlRequest.httpMethod = "POST"
                                urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type") // change as per server requirements
                                urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
                                urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
                                
                                let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
                                    // do stuff
                                    defer {sem.signal()}
                                    
//                                    print("doc data: ", allData["dateCreated"])
                                    if let data = data {
                                        let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                                        if let dataDict = dataJsonObject as? [String: Any] {
                                            if let array = dataDict["results"] as? [[String: Any]] {
                                                allData["locations"] = array
                                                self.prevAdventours.append(allData)
                                                DispatchQueue.main.async {
                                                    self.prevAdventoursSource = PrevAdventoursDataSource(withDataSource: self.prevAdventours)
                                                    self.prevAdventoursTable?.dataSource = self.prevAdventoursSource
                                                    self.prevAdventoursTable?.reloadData()
                                                    self.activityIndicatorAdventours?.stopAnimating()
                                                    self.prevAdventoursTable?.isHidden = false
                                                    self.noAdventoursMessage?.isHidden = true
                                                }
                                                
                                            }
                                        }
                                    }
                                }
                                task.resume()
                            }
                        }
                        sem.wait()
//                        print("This is all the data: ", allData ?? "None")
                    }
                }
            }
    }
    
    func getBeaconsData() {
        var allData: [String: Any]!
        self.beacons = []
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        db.collection("Adventourists")
            .document(self.user.uid)
            .collection("beacons")
            .order(by: "dateCreated", descending: true)
            .limit(to: 5)
            .getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                    self.activityIndicatorBeacons?.stopAnimating()
                    self.beaconsTable?.isHidden = false
                } else if (snap!.isEmpty) {
                    self.activityIndicatorBeacons?.stopAnimating()
                    self.beaconsTable?.isHidden = false
                    self.noBeaconsMessage?.isHidden = false
                } else {
                    for doc in snap!.documents {
//                        print("The data", doc.data())
                        let documentID = doc.documentID
                        if let data = doc.data() as? [String: Any] {
                            allData = data
                            allData.updateValue(documentID, forKey: "documentID")
//                            print(allData)
                            if let locations = data["locations"] as? [String] {
                                let params: [String: Any] = [
                                    "uid": self.user!.uid,
                                    "ids": locations
                                ]
                                
                                let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-foursquare-places")
                                var urlRequest = URLRequest(url: url!)
                                urlRequest.httpMethod = "POST"
                                urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type") // change as per server requirements
                                urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
                                urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
                                
                                let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
                                    // do stuff
                                    defer {sem.signal()}
                                    
//                                    print("doc data: ", allData["dateCreated"])
                                    if let data = data {
                                        let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                                        if let dataDict = dataJsonObject as? [String: Any] {
                                            if let array = dataDict["results"] as? [[String: Any]] {
                                                allData["locations"] = array
//                                                print("date: ", allData["dateCreated"]!)
                                                self.beacons.append(allData)
                                                DispatchQueue.main.async {
//                                                    print(allData["numLocations"])
                                                    
                                                    self.beaconsSource = BeaconsDataSource(withDataSource: self.beacons)
                                                    self.beaconsTable?.dataSource = self.beaconsSource
//                                                    print(self.prevAdventours)
                                                    self.beaconsTable?.reloadData()
                                                    self.activityIndicatorBeacons?.stopAnimating()
                                                    self.beaconsTable?.isHidden = false
                                                    self.noBeaconsMessage?.isHidden = true
                                                    
                                                }
                                                
                                            }
                                            
                                        }
                                    }
                                }
                                task.resume()
                            }
                        }
                        sem.wait()
                    }
                }
            }
    }
    
    @IBAction func unwindToProfile(sender: UIStoryboardSegue) {
        
    }
}
