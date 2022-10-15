import UIKit
import MapKit
import Alamofire
import FirebaseAuth

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

        // Do your stuff here
     }
    
    
}



class MapViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    
    
    @IBOutlet weak var mapView: MKMapView!
    var locations: [[String: Any]] = []
    var ids: [String] = ["4c06dcb92e80a59384c774f9",
                         "4c324f2aed37a59360a76b03",
                         "50a9122190e76d9fc7558c05",
                         "562a9519498ed4a52fb6b936"]
    
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var addButton: UIButton!
    
    var user: User!
    var callback: (([String]) ->())?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            self.performSegue(withIdentifier: "LoginNavigationController", sender: self)
        }
        
        self.locationsTable.delegate = self
        self.locationsTable.dataSource = self
        getLocationData()
                
        
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
    
    
    
    
    
    func getLocationData() {
        
        
        
        let params: [String: Any] = [
            "uid": user!.uid,
            "ids": ids
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
                            }

                        }
                        
                    }
                }
            }
            task.resume()
        }
    }
}
    
