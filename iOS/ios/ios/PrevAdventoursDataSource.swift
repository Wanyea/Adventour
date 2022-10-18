//
//  PrevAdventoursDataSource.swift
//  ios
//
//  Created by Glenn Hartwell on 10/15/22.
//

import UIKit

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
        if let dateCreated = data[indexPath.item]["dateCreated"] as? String {
//            print("Date created: ", data)
            cell.dateCreated.text = dateCreated
        }
        cell.location1.text = ""
        cell.location2.text = ""
        cell.location3.text = ""
        return cell
    }
    

}
