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
        cell.dateCreated.text = ""
        cell.location1.text = ""
        cell.location2.text = ""
        cell.location3.text = ""
        return cell
    }
    

}
