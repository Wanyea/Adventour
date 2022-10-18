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
    
    @IBOutlet weak var username: UILabel!
    @IBOutlet weak var birthday: UILabel!
    @IBOutlet weak var birthdayIcon: UILabel!
    @IBOutlet weak var mantra: UILabel!
    
    @IBOutlet weak var prevAdventoursTable: UITableView!
    @IBOutlet weak var beaconsTable: UITableView!
    @IBOutlet weak var activityIndicatorInfo: UIActivityIndicatorView!
    @IBOutlet weak var activityIndicatorAdventours: UIActivityIndicatorView!
    @IBOutlet weak var activityIndicatorBeacons: UIActivityIndicatorView!
    
    var prevAdventours: [[String: Any]] = [[:]]
    var beacons: [[String: Any]] = [[:]]
    
    var prevAdventoursSource: UITableViewDataSource!
    var beaconsSource: UITableViewDataSource!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        self.prevAdventoursTable.delegate = self
//        self.prevAdventoursTable.register(PrevAdventoursTableViewCell.self, forCellReuseIdentifier: "PrevAdventourCell")
        
        
//        self.beaconsTable.delegate = self
//        beaconsSource = PrevAdventoursDataSource(withDataSource: self.beacons)
//        self.beaconsTable.dataSource = beaconsSource
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.hideUserInfo()
        self.prevAdventoursTable?.isHidden = true
        self.beaconsTable?.isHidden = true
        self.activityIndicatorInfo?.startAnimating()
        self.activityIndicatorAdventours?.startAnimating()
        self.activityIndicatorBeacons?.startAnimating()
        getUserData()
        getPrevAdventourData()
        getBeaconsData()
    }
    
    func getUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
//                    print(user)
                    if let birthday = user["birthday:"] as? String {
//                        print(birthday)
                        self.birthday?.text = birthday
                    }
                    if let username = user["nickname"] as? String {
//                        print(username)
                        self.username?.text = username
                    }
                    if let mantra = user["mantra"] as? String {
//                        print(mantra)
                        self.mantra?.text = mantra
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
        self.username.isHidden = true
        self.birthday.isHidden = true
        self.mantra.isHidden = true
        self.birthdayIcon.isHidden = true
    }
    
    func showUserInfo() {
        self.username.isHidden = false
        self.birthday.isHidden = false
        self.mantra.isHidden = false
        self.birthdayIcon.isHidden = false
    }
    
    func getPrevAdventourData() {
        
        var allData: [String: Any]!
        
        let db = Firestore.firestore()
        db.collection("Adventourists")
            .document(self.user.uid)
            .collection("adventours")
            .getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                } else {
                    for doc in snap!.documents {
//                        print("The data", doc.data())
                        if let data = doc.data() as? [String: Any] {
                            allData = data
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
                                    
                                    if let data = data {
                                        let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                                        if let dataDict = dataJsonObject as? [String: Any] {
                                            if let array = dataDict["results"] as? [[String: Any]] {
                                                allData["locations"] = array
//                                                print("All data: ", allData!)
                                                
                                                DispatchQueue.main.async {
                                                    print(allData["numLocations"])
                                                    self.prevAdventours.append(allData)
                                                    
                                                    
                                                }
                                            }
                                            
                                        }
                                    }
                                }
                                task.resume()
                            }
                            
                        }
//                        print("This is all the data: ", allData ?? "None")
                        
                    }
                    DispatchQueue.main.async {
                        print("Length of prev adventours: ", self.prevAdventours.count)
                        self.prevAdventoursSource = PrevAdventoursDataSource(withDataSource: self.prevAdventours)
                        self.prevAdventoursTable.dataSource = self.prevAdventoursSource
                        self.prevAdventoursTable.reloadData()
                        self.activityIndicatorAdventours?.stopAnimating()
                        self.prevAdventoursTable.isHidden = false
                    }
                    
                }
            }
        
    
    
    
    }
    
    
    
    func getBeaconsData() {
        let db = Firestore.firestore()
        db.collection("Adventourists")
            .document(self.user.uid)
            .collection("adventours")
            .getDocuments { snap, error in
                if let error = error {
                    print("Error getting documents \(error)")
                } else {
                    for doc in snap!.documents {
                        let data = doc.data().map { key, value in
//                            print("Key: ", key, " Value:", value)
                            
                            self.activityIndicatorBeacons?.stopAnimating()
                            self.beaconsTable.isHidden = false
                        }
                    }
                }
            }
    }

}
