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
    
    var adventourNextPageListener: ListenerRegistration!
    var beaconNextPageListener: ListenerRegistration!
    
    var user: User!
    @IBOutlet weak var exitIndicator: UIView!
    
    @IBOutlet weak var profilePic: UIImageView!
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
    
    var adventourQuery: Query!
    var beaconQuery: Query!
    var userDidLoad: Bool = false
    
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
        self.prevAdventours = []
        
        DispatchQueue.global(qos: .userInitiated).async {
            Task {
                await self.getUserData()
                await self.getPrevAdventourData()
                await self.getBeaconsData()
            }
        }
    }
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        if scrollView is AdventourTableView {
            // UITableView only moves in one direction, y axis
            let currentOffset = scrollView.contentOffset.y
            let maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height

            // Change number to adjust the distance from bottom, more negative = further pull
            if maximumOffset - currentOffset <= -40.0 {
                let spinner = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                spinner.startAnimating()
                spinner.frame = CGRect(x: CGFloat(0), y: CGFloat(0), width: self.prevAdventoursTable.bounds.width, height: CGFloat(44))

                self.prevAdventoursTable.tableFooterView = spinner
                self.prevAdventoursTable.tableFooterView?.isHidden = false
                getNextPagePrevAdventours()
            }
        } else if scrollView is BeaconTableView {
            // UITableView only moves in one direction, y axis
            let currentOffset = scrollView.contentOffset.y
            let maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height

            // Change number to adjust the distance from bottom, more negative = further pull
            if maximumOffset - currentOffset <= -40.0 {
                let spinner = UIActivityIndicatorView(style: UIActivityIndicatorView.Style.medium)
                spinner.startAnimating()
                spinner.frame = CGRect(x: CGFloat(0), y: CGFloat(0), width: self.beaconsTable.bounds.width, height: CGFloat(44))

                self.beaconsTable.tableFooterView = spinner
                self.beaconsTable.tableFooterView?.isHidden = false
                getNextPageBeacons()
            }
        }
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if tableView is AdventourTableView {
            let storyboard = UIStoryboard(name: "AdventourSummaryPage", bundle: nil)

            if let locations = prevAdventours[indexPath.item]["locations"] as? [[String: Any]] {
                print("SUCCESSFULLY RETRIEVED LOCATIONS")
                if let vc = storyboard.instantiateViewController(identifier: "AdventourSummary") as? AdventourSummaryViewController {
                    vc.source = self
                    vc.locations = locations
                    if let ids = prevAdventours[indexPath.item]["ids"] as? [String] {
                        print("IDS ARE HERE ", ids)
                        vc.ids = ids
                    }
                    if let beaconLocation = prevAdventours[indexPath.item]["beaconLocation"] as? String {
                        vc.beaconLocation = beaconLocation
                    }
                    if let isBeacon = prevAdventours[indexPath.item]["isBeacon"] as? Bool {
                        vc.isBeacon = isBeacon
                    }
                    if let documentID = prevAdventours[indexPath.item]["documentID"] as? String {
                        vc.documentID = documentID
                    }
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
    
    func getUserData() async {
        
        if userDidLoad {
            self.activityIndicatorInfo?.stopAnimating()
            self.showUserInfo()
        } else {
            userDidLoad = true
        }
        
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
//                    print(user)
                    let dateFormatter = DateFormatter()
                    dateFormatter.dateFormat = "MM/dd/YYYY"
                    if let firTimestamp = user["birthdate"] as? Timestamp {
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
                    if let iosPfpRef = user["iosPfpRef"] as? String {
                        self.profilePic.image = UIImage(named: iosPfpRef)
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
        self.profilePic?.isHidden = true
    }
    
    func showUserInfo() {
        self.nicknameLabel?.isHidden = false
        self.birthdayLabel?.isHidden = false
        self.mantraLabel?.isHidden = false
        self.birthdayIcon?.isHidden = false
        self.profilePic?.isHidden = false
    }
    
    func getNextPagePrevAdventours() {
        self.adventourNextPageListener = self.adventourQuery.addSnapshotListener { (snapshot, error) in
            guard let snapshot = snapshot else {
                print("\(error.debugDescription)")
                return
            }

            guard let lastSnapshot = snapshot.documents.last else {
                // The collection is empty.
                self.prevAdventoursTable.tableFooterView?.isHidden = true
                return
            }

            var allData: [String: Any]!
            let sem = DispatchSemaphore(value: 0)
            
            let db = Firestore.firestore()
            self.adventourQuery = db.collection("Adventourists")
                .document(self.user.uid)
                .collection("adventours")
                .order(by: "dateCreated", descending: true)
                .limit(to: 5)
                .start(afterDocument: lastSnapshot)
            
            self.adventourQuery.getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                    self.activityIndicatorAdventours?.stopAnimating()
                    self.prevAdventoursTable?.isHidden = false
                    self.prevAdventoursTable.tableFooterView?.isHidden = true
                } else if snap!.isEmpty {
                    print("EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY EMPTY")
                    self.activityIndicatorAdventours?.stopAnimating()
                    self.prevAdventoursTable?.isHidden = false
                    self.prevAdventoursTable.tableFooterView?.isHidden = true
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
                                                allData["ids"] = allData["locations"]
                                                allData["locations"] = array
                                                self.prevAdventours.append(allData)
                                                DispatchQueue.main.async {
                                                    self.prevAdventoursSource = PrevAdventoursDataSource(withDataSource: self.prevAdventours)
                                                    self.prevAdventoursTable?.dataSource = self.prevAdventoursSource
                                                    self.prevAdventoursTable?.reloadData()
                                                    self.activityIndicatorAdventours?.stopAnimating()
                                                    self.prevAdventoursTable?.isHidden = false
                                                    self.noAdventoursMessage?.isHidden = true
                                                    self.prevAdventoursTable.tableFooterView?.isHidden = true
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
                        self.adventourNextPageListener.remove()
                    }
                }
            }
        }
    }
    
    func getPrevAdventourData() async {
        
        var allData: [String: Any]!
        
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        self.adventourQuery = db.collection("Adventourists")
            .document(self.user.uid)
            .collection("adventours")
            .order(by: "dateCreated", descending: true)
            .limit(to: 5)
        
        self.adventourQuery.getDocuments { snap, error in
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
                                                allData["ids"] = allData["locations"]
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
    
    func getNextPageBeacons() {
        self.beaconNextPageListener = self.beaconQuery.addSnapshotListener { (snapshot, error) in
            guard let snapshot = snapshot else {
                print("\(error.debugDescription)")
                return
            }

            guard let lastSnapshot = snapshot.documents.last else {
                // The collection is empty.
                self.beaconsTable.tableFooterView?.isHidden = true
                return
            }

            var allData: [String: Any]!
            let sem = DispatchSemaphore(value: 0)
            
            let db = Firestore.firestore()
            self.beaconQuery = db.collection("Adventourists")
                .document(self.user.uid)
                .collection("beacons")
                .order(by: "dateCreated", descending: true)
                .limit(to: 5)
                .start(afterDocument: lastSnapshot)
            
            self.beaconQuery.getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                    self.activityIndicatorBeacons?.stopAnimating()
                    self.beaconsTable.tableFooterView?.isHidden = true
                } else if (snap!.isEmpty) {
                    self.activityIndicatorBeacons?.stopAnimating()
                    self.beaconsTable?.isHidden = false
                    self.beaconsTable.tableFooterView?.isHidden = true
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
                                                    self.beaconsTable.tableFooterView?.isHidden = true
                                                }
                                                
                                            }
                                            
                                        }
                                    }
                                }
                                task.resume()
                            }
                        }
                        sem.wait()
                        self.beaconNextPageListener.remove()
                    }
                }
            }
        }
    }
    
    func getBeaconsData() async {
        self.beacons = []
        var allData: [String: Any]!
        
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        self.beaconQuery = db.collection("Adventourists")
            .document(self.user.uid)
            .collection("beacons")
            .order(by: "dateCreated", descending: true)
            .limit(to: 5)
        
        self.beaconQuery.getDocuments { snap, error in
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
                                                
                                                self.beacons.append(allData)
                                                print("data: ", self.beacons)
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
    @IBAction func unwindToProfile(_ segue: UIStoryboardSegue) {
        
    }
}
