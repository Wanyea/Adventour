import UIKit
import MapKit

import FirebaseAuth
import FirebaseFirestore

extension MapViewController: MapTableViewCellDelegate {
    
     func onTouchShowButton(from cell: MapTableViewCell) {
         let location = CLLocationCoordinate2D(latitude: cell.lat, longitude: cell.lon)
         
         let span = MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
         
         let region = MKCoordinateRegion(center: location, span: span)
         let annotation = MKPointAnnotation()
         annotation.coordinate = CLLocationCoordinate2D(latitude: cell.lat, longitude: cell.lon)
         self.mapView?.setRegion(region, animated: true)
         self.mapView?.removeAnnotations(self.mapView.annotations)
         self.mapView?.addAnnotation(annotation)

     }
    
    
}

class MapViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    @IBOutlet weak var mapView: MKMapView!
    var locations: [[String: Any]] = []
    var ids: [String]!
    
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var addButton: UIButton!
    
    var user: User!
    var beaconLocation: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            print("No user")
            self.switchToLoggedOut()
        }
        
        self.locationsTable.delegate = self
        self.locationsTable.dataSource = self
        
        
                
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationItem.setHidesBackButton(true, animated: true)
        if let ids = self.ids {
            print("Map, These is the ids: ", ids)
        } else {
            print("Map, The ids is nil")
        }
        self.hideTable()
        getLocationData()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? CongratsViewController {
            destinationVC.locations = self.ids
            destinationVC.beaconLocation = self.beaconLocation
        }
        
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return locations.count + 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.item < locations.count) {
            let cell = locationsTable.dequeueReusableCell(withIdentifier: "MapCell", for: indexPath) as! MapTableViewCell
            cell.locationName.text = locations[indexPath.item]["name"] as? String
            cell.delegate = self
            // Safely unwrap Json Dictionary
            if let geocodes = locations[indexPath.item]["geocodes"] as? [String: Any] {
                if let main = geocodes["main"] as? [String: Any] {
                    if let lat = main["latitude"] as? Double {
                        cell.lat = lat
//                        print("The cell's lat is: ", cell.lat!)
                    }
                    if let lon = main["longitude"] as? Double {
                        cell.lon = lon
//                        print("The cell's lon is: ", cell.lon!)
                    }
                }
            }
            
            if (indexPath.item == 0) {
                let location = CLLocationCoordinate2D(latitude: cell.lat, longitude: cell.lon)
                
                let span = MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
                
                let region = MKCoordinateRegion(center: location, span: span)
                let annotation = MKPointAnnotation()
                annotation.coordinate = CLLocationCoordinate2D(latitude: cell.lat, longitude: cell.lon)
                
                self.mapView?.setRegion(region, animated: true)
                self.mapView?.addAnnotation(annotation)
            }
            return cell
        }
        else {
            let cell = locationsTable.dequeueReusableCell(withIdentifier: "FinishCell", for: indexPath) as! FinishCell

            return cell
        }
    }
    
    
    @IBAction func saveAdventour(_ sender: Any) {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        
        let adventour: [String: Any] = [
            "locations": self.ids,
            "beaconLocation": self.beaconLocation,
            "dateCreated": Date(),
            "numLocations": self.locations.count,
        ]
        
        let db = Firestore.firestore()
        
        db.collection("Adventourists")
            .document(self.user.uid)
            .collection("adventours")
            .addDocument(data: adventour) {
                err in
                if let err = err {
                    print("Error writing document: \(err)")
                } else {
                    print("Document successfully written!")
                }
            }
    }
    
    func hideTable() {
        self.locationsTable.isHidden = true
    }
        
    func showTable() {
        self.locationsTable.isHidden = false
    }
    
    func getLocationData() {
        
        self.activityIndicator.startAnimating()
        
        let params: [String: Any] = [
            "uid": user!.uid,
            "ids": self.ids!
        ]
        
        if (ids.isEmpty) {
            self.navigationController?.popToRootViewController(animated: true)
        } else {
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
                                self.activityIndicator.stopAnimating()
                                
                            }

                        }
                        
                    }
                }
            }
            task.resume()
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
    
