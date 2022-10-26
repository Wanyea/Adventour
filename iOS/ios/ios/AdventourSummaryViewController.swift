//
//  AdventourSummaryViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/19/22.
//

import UIKit
import FirebaseAuth

class AdventourSummaryViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {

    var locations: [String]!
    var locationsData: [[String: Any]] = []
    var user: User!
    
    @IBOutlet weak var locationsTable: UITableView!
    
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
        getAdventourData()
        // Do any additional setup after loading the view.
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return locationsData.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {

        let cell = locationsTable.dequeueReusableCell(withIdentifier: "SummaryCell", for: indexPath) as! SummaryTableViewCell
        if let name = locationsData[indexPath.item]["name"] as? String {
            cell.name.text = name
        }
        if let description = locationsData[indexPath.item]["description"] as? String {
            cell.descriptionLabel.text = description
        } else {
            cell.descriptionLabel.text = "This place doesn't have a listed description"
        }
        
        // Safely unwrap Json Dictionary
        
        return cell
    }
    
    func getAdventourData() {
        
        let params: [String: Any] = [
            "uid": user!.uid,
            "ids": self.locations!
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
                        self.locationsData = array
                        DispatchQueue.main.async {
                            self.locationsTable.reloadData()
                            self.showTable()
                            self.activityIndicator.stopAnimating()
                            
                        }

                    }
                    
                }
            }
        }
        task.resume()
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
