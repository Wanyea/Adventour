//
//  AdventourSummaryViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/19/22.
//

import UIKit
import FirebaseAuth

class AdventourSummaryViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var ids: [String] = []
    var locations: [[String: Any]] = []
    var user: User!
    var source: UIViewController!
    var beaconLocation: String!
    var isBeacon: Bool!
    var documentID: String!
    
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var postButton: UIButton!
    
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.isNavigationBarHidden = false
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            print("No user")
            self.switchToLoggedOut()
        }
        
        self.hideTable()
        self.locationsTable.delegate = self
        self.locationsTable.dataSource = self
        self.postButton.isHidden = true
        getAdventourData()
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        print(locations.count)
        return locations.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let cell = locationsTable.dequeueReusableCell(withIdentifier: "SummaryCell", for: indexPath) as! SummaryTableViewCell
        if let name = locations[indexPath.item]["name"] as? String {
            cell.name.text = name
        }
        if let description = locations[indexPath.item]["description"] as? String {
            cell.descriptionLabel.text = description
        } else {
            cell.descriptionLabel.text = "This place doesn't have a listed description"
        }
        if let rating = locations[indexPath.item]["rating"] as? Double {
            cell.cosmosView.rating = rating / 2
        }
        let sem = DispatchSemaphore(value: 0)
        if let photos = locations[indexPath.item]["photos"] as? [[String: Any]] {
            if photos.count > 0 {
                let photo = photos[0]
                if let prefix = photo["prefix"] as? String {
                    if let suffix = photo["suffix"] as? String {
                        let url = prefix + "original" + suffix
                        cell.img.loadFrom(URLAddress: url, semaphore: sem)
                    }
                }
            }
        }
        sem.wait()
        
        
        return cell
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? BeaconPostViewController {
            print("LOCATIONS ", self.locations)
            dest.locations = self.locations
            dest.ids = self.ids
            dest.beaconLocation = self.beaconLocation
            dest.isBeacon = self.isBeacon
            dest.isEditing = true
            dest.source = self
            dest.beaconInfo["documentID"] = self.documentID
        }
    }
    
    func checkBeacon() {
        if let isBeacon = self.isBeacon {
            if (isBeacon) {
                self.postButton.isHidden = true
            } else {
                self.postButton.isHidden = false
            }
        } else {
            self.postButton.isHidden = false
        }
    }
    
    func getAdventourData() {
        
        self.activityIndicator?.isHidden = false
        
        if (ids.isEmpty && locations.isEmpty) {
            self.navigationController?.popToRootViewController(animated: true)
        } else if (!locations.isEmpty) {
            self.locationsTable.reloadData()
            self.activityIndicator?.stopAnimating()
            self.locationsTable.isHidden = false
            return
        } else {
            let params: [String: Any] = [
                "uid": user!.uid,
                "ids": self.ids
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
                            self.locations = array
                            DispatchQueue.main.async {
                                self.locationsTable.reloadData()
                                self.showTable()
                                self.checkBeacon()
                                self.activityIndicator.stopAnimating()
                                
                            }

                        }
                        
                    }
                }
            }
            task.resume()
        }
    }
    
    func hideTable() {
        self.locationsTable.isHidden = true
    }
        
    func showTable() {
        self.locationsTable.isHidden = false
    }
    
    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoggedOutViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc)
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
