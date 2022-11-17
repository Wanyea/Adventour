//
//  BeaconsDataSource.swift
//  ios
//
//  Created by Glenn Hartwell on 10/15/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class BeaconsDataSource: NSObject, UITableViewDataSource {
    
    var data: [[String: Any]]
    
    var user: User!
    
    var listener: ListenerRegistration!
    
    init(withDataSource data: [[String: Any]]) {
        self.data = data
        
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        
    }
    
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BeaconBoardCell", for: indexPath) as! BeaconBoardTableViewCell
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        if let firTimestamp = data[indexPath.item]["dateCreated"] as? Timestamp {
            let swiftDate = firTimestamp.dateValue()
//            print("Date created: ", data)
            let stringDate = dateFormatter.string(from: swiftDate)
            cell.dateCreated.text = stringDate
        }
        if let title = data[indexPath.item]["title"] as? String {
            cell.title.text = title
        }
        if let intro = data[indexPath.item]["intro"] as? String {
            cell.intro.text = intro
        }
        getBeaconPicture(beaconInfo: data[indexPath.item], cell: cell)
        setNumLikes(beaconInfo: data[indexPath.item], cell: cell)
        checkLiked(beaconInfo: data[indexPath.item], cell: cell)
        loadUserData(cell: cell)
        return cell
    }
    
    func loadUserData(cell: BeaconBoardTableViewCell) {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                if let data = document.data() {
                    if let nickname = data["nickname"] as? String {
                        cell.nickname.text = nickname
                    }
                    if let pfpRef = data["iosPfpRef"] as? String {
                        cell.authorProfilePic.image = UIImage(named: pfpRef)
                    }
                }
            }
        }
        
    }
    
    func getBeaconPicture(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell) {
        let sem = DispatchSemaphore(value: 0)
        print("BEACON INFO: \(beaconInfo)")
        if let locations = beaconInfo["locations"] as? [[String: Any]] {
            if locations.count > 0 {
                let location = locations[0]
                if let photos = location["photos"] as? [[String: Any]] {
                    print("photos: \(photos)")
                    for pic in photos {
                        if let prefix = pic["prefix"] as? String {
                            if let suffix = pic["suffix"] as? String {
                                let url = prefix + "original" + suffix
                                cell.beaconPicture.loadFrom(URLAddress: url, semaphore: sem)
                                return
                            }
                        }
                        
                    }
                }
            }
        }
    }
    
    func checkLiked(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell) {
        
        let db = Firestore.firestore()
        var query: Query = db.collection("Likes")
            .whereField("uid", isEqualTo: self.user.uid)
            .whereField("beaconID", isEqualTo: beaconInfo["documentID"])
            
        self.listener = query.addSnapshotListener { snap, error in
            if let error = error {
                print(error)
                self.listener.remove()
            } else if snap!.isEmpty {
                cell.likeIcon.image = UIImage(systemName: "heart")
                self.listener.remove()
                
            } else {
                cell.likeIcon.image = UIImage(systemName: "heart.fill")
                self.listener.remove()
            }
        }
    }
    
    func setNumLikes(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell)  {
        let db = Firestore.firestore()
        let beaconRef = db.collection("Beacons").document(beaconInfo["documentID"] as! String)
        getNumLikes(ref: beaconRef, cell: cell)
    }
    
    func getNumLikes(ref: DocumentReference, cell: BeaconBoardTableViewCell) {
        print("Calling getNumLikes...")
        var totalCount: Int = 0
        ref.collection("likeShards").getDocuments() { (querySnapshot, err) in
            
            if err != nil {
                // Error getting shards
                // ...
                return
            } else {
                for document in querySnapshot!.documents {
                    let count = document.data()["likes"] as! Int
                    totalCount += count
                }
            }

            cell.numLikes.text = String(totalCount)
        }
        
    }
    

}
