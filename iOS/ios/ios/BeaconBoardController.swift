//
//  BeaconBoardController.swift
//  ios
//
//  Created by Cassidy Moore on 10/20/22.
//

import Foundation
import GooglePlaces
import FirebaseAuth
import FirebaseFirestore

class BeaconBoardViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    var beaconLocation = ""
    var lon: Double!
    var lat: Double!
    
    
    
    // Card Outlets
    private var placesClient: GMSPlacesClient!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var beaconsTable: UITableView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var errorMessage: UILabel!
    
    var beacons: [[String: Any]] = []
    var user: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            print("No user")
            self.switchToLoggedOut()
        }
        self.beaconsTable.delegate = self
        self.beaconsTable.dataSource = self
        // Do any additional setup after loading the view.
        placesClient = GMSPlacesClient.shared()
        searchBar?.text = self.beaconLocation
    }
    
    @IBAction func getCurrentPlace(_ sender: Any){
        let autocompleteController = GMSAutocompleteViewController()
        autocompleteController.delegate = self
        let fields: GMSPlaceField = GMSPlaceField(rawValue: UInt(GMSPlaceField.coordinate.rawValue) |  UInt(GMSPlaceField.name.rawValue) | UInt(GMSPlaceField.formattedAddress.rawValue) | UInt(GMSPlaceField.placeID.rawValue))
        
        autocompleteController.placeFields = fields
        
        
        let filter = GMSAutocompleteFilter()
        filter.countries = ["us"]
        filter.type = .city
        
        autocompleteController.autocompleteFilter = filter
        present(autocompleteController,animated: true, completion: nil)
        
        
    }
    @IBAction func updateSearch()
    {
        searchBar?.text = self.beaconLocation
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return beacons.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "BeaconPost", bundle: nil)
        print(beacons[indexPath.item])
        if let locations = beacons[indexPath.item]["locations"] as? [[String: Any]] {
            print("SUCCESSFULLY RETRIEVED LOCATIONS")
            if let vc = storyboard.instantiateViewController(identifier: "BeaconPost") as? BeaconPostViewController {
                vc.locations = locations
                vc.source = self
                vc.beacons = self.beacons
                self.navigationController?.pushViewController(vc, animated: true)
            }
            
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.beaconsTable.dequeueReusableCell(withIdentifier: "BeaconBoardCell", for: indexPath) as! BeaconBoardTableViewCell
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        print(beacons[indexPath.item]["documentID"])
        if let firTimestamp = beacons[indexPath.item]["dateCreated"] as? Timestamp {
            if let swiftDate = firTimestamp.dateValue() as? Date {
    //            print("Date created: ", data)
                if let stringDate = dateFormatter.string(from: swiftDate) as? String {
                    cell.dateCreated.text = stringDate
                }
                
            }
        }
        if let locations = beacons[indexPath.item]["locations"] as? [[String: Any]] {
            if let location1 = locations[0] as? [String: Any] {
                if let name = location1["name"] as? String {
                    cell.location1.text = "⦿ " + name
                } else {
                    cell.location1.text = "This location does not have a name..."
                }
            } else {
                cell.location1.text = "This Adventour does not have any locations"
            }
            if let location2 = locations[1] as? [String: Any] {
                if let name = location2["name"] as? String {
                    cell.location2.text = "⦿ " + name
                } else {
                    cell.location2.text = "This location does not have a name..."
                }
            }
            if let location3 = locations[2] as? [String: Any] {
                if let name = location3["name"] as? String {
                    cell.location3.text = "⦿ " + name
                } else {
                    cell.location3.text = "This location does not have a name..."
                    
                }
            }
        } else {
            print("No locations")
        }
        
        return cell
    }
    
    func getBeacons() {
        self.activityIndicator.isHidden = false
        self.errorMessage.isHidden = true
        self.activityIndicator.startAnimating()
        print("GET BEACONS IS RUNNING")
        var allData: [String: Any]!
        self.beacons = []
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        
        db.collection("Beacons")
            .whereField("beaconLocation", isEqualTo: self.beaconLocation)
            .getDocuments() { (querySnapshot, err) in
                    if let err = err {
                        print("Error getting documents: \(err)")
                    } else {
                        if querySnapshot!.isEmpty {
                            DispatchQueue.main.async {
                                self.beaconsTable?.reloadData()
                                self.activityIndicator.stopAnimating()
                                self.errorMessage.text = "There are no Beacons for this location!"
                                self.errorMessage.isHidden = false
                                
                            }
                        } else {
                            for document in querySnapshot!.documents {
                                print("\(document.documentID) => \(document.data())")
                                let documentID = document.documentID
                                allData = document.data()
                                allData.updateValue(documentID, forKey: "documentID")
                                if let locations = allData["locations"] as? [String] {
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
                                        
                                        print("doc data: ", allData["dateCreated"])
                                        if let data = data {
                                            let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                                            if let dataDict = dataJsonObject as? [String: Any] {
                                                if let array = dataDict["results"] as? [[String: Any]] {
                                                    allData["locations"] = array
                                                    self.beacons.append(allData)
                                                    print("Count: ", self.beacons.count)
                                                    DispatchQueue.main.async {
                                                        self.beaconsTable?.reloadData()
                                                        self.activityIndicator.stopAnimating()
                                                    }
                                                    
                                                }
                                                
                                            }
                                        }
                                    }
                                    task.resume()
                                }
                                
                                sem.wait()
                            }
                        }
                        
                    }
            }
    }
    
    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoggedOutViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc)
    }
        
}
    
 
extension BeaconBoardViewController: GMSAutocompleteViewControllerDelegate {

    
    
  // Handle the user's selection.
  func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
    print("Place name: \(place.name)")
    print("Place ID: \(place.placeID)")
    print("Place attributions: \(place.attributions)")
    print("Place address: \(place.formattedAddress))")
    self.lat = place.coordinate.latitude
    self.lon = place.coordinate.longitude
    print("Place lat: \(self.lat)")
    print("Place lon: \(self.lon)")
    self.beaconLocation = place.formattedAddress!
    updateSearch()
    getBeacons()
    dismiss(animated: true, completion: nil)
    
  }

  func viewController(_ viewController: GMSAutocompleteViewController, didFailAutocompleteWithError error: Error) {
    // TODO: handle the error.
    print("Error: ", error.localizedDescription)
  }

  // User canceled the operation.
  func wasCancelled(_ viewController: GMSAutocompleteViewController) {
    dismiss(animated: true, completion: nil)
  }

  // Turn the network activity indicator on and off again.
  func didRequestAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
    UIApplication.shared.isNetworkActivityIndicatorVisible = true
  }

  func didUpdateAutocompletePredictions(_ viewController: GMSAutocompleteViewController) {
    UIApplication.shared.isNetworkActivityIndicatorVisible = false
  }

}
