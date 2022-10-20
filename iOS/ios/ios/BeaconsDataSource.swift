//
//  BeaconsDataSource.swift
//  ios
//
//  Created by Glenn Hartwell on 10/15/22.
//

import UIKit

class BeaconsDataSource: NSObject, UITableViewDataSource {
    
    var data: [[String: Any]]
    
    init(withDataSource data: [[String: Any]]) {
        self.data = data
    }

    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return data.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "BeaconCell", for: indexPath) as! BeaconsTableViewCell
        if let dateCreated = data[indexPath.item]["dateCreated"] as? String {
//            print("Date created: ", data)
            cell.dateCreated.text = dateCreated
        }

        if let locations = data[indexPath.item]["locations"] as? [[String: Any]] {
            if let location1 = locations[0] as? [String: Any] {
                if let name = location1["name"] as? String {
                    cell.location1.text = "• " + name
                } else {
                    cell.location1.text = "This location does not have a name..."
                }
            } else {
                cell.location1.text = "This Adventour does not have any locations"
            }
            if let location2 = locations[1] as? [String: Any] {
                if let name = location2["name"] as? String {
                    cell.location2.text =  "• " + name
                } else {
                    cell.location2.text = "This location does not have a name..."
                }
            }
            if let location3 = locations[2] as? [String: Any] {
                if let name = location3["name"] as? String {
                    cell.location3.text = "• " + name
                } else {
                    cell.location3.text = "This location does not have a name..."
                }
            }
        } else {
            print("No locations")
        }
        return cell
    }
    

}