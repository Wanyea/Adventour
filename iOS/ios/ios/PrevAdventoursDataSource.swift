//
//  PrevAdventoursDataSource.swift
//  ios
//
//  Created by Glenn Hartwell on 10/15/22.
//

import UIKit
import FirebaseFirestore

class PrevAdventoursDataSource: NSObject, UITableViewDataSource {
    
    var data: [[String: Any]]
    
    init(withDataSource data: [[String: Any]]) {
//        print("data: ", data)
        self.data = data
    }

    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "PrevAdventourCell", for: indexPath) as! PrevAdventoursTableViewCell
//        print(data[indexPath.item])
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        
        
        if let firTimestamp = data[indexPath.item]["dateCreated"] as? Timestamp {
            if let swiftDate = firTimestamp.dateValue() as? Date {
    //            print("Date created: ", data)
                if let stringDate = dateFormatter.string(from: swiftDate) as? String {
                    cell.dateCreated.text = stringDate
                }
                
            }
        }

        if let locations = data[indexPath.item]["locations"] as? [[String: Any]] {
            if locations.count > 0 {
                if let location1 = locations[0] as? [String: Any] {
                    if let name = location1["name"] as? String {
                        cell.location1.text = name
                    } else {
                        cell.location1.text = ""
                    }
                } else {
                    cell.location1.text = "This Adventour does not have any locations"
                }
            } else {
                cell.location1.text = ""
            }
            if locations.count > 1 {
                if let location2 = locations[1] as? [String: Any] {
                    if let name = location2["name"] as? String {
                        cell.location2.text = name
                    } else {
                        cell.location2.text = ""
                    }
                }
            } else {
                cell.location2.text = ""
            }
            if locations.count > 2 {
                if let location3 = locations[2] as? [String: Any] {
                    if let name = location3["name"] as? String {
                        cell.location3.text = name
                    } else {
                        cell.location3.text = ""
                        
                    }
                }
            } else {
                cell.location3.text = ""
            }
        } else {
            print("No locations")
        }
        return cell
    }
    

}
