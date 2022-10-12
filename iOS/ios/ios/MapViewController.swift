import UIKit
import MapKit
import Alamofire
import FirebaseAuth

class MapViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet weak var mapView: MKMapView!
    var locations: [[String: Any]] = []
    
    @IBOutlet weak var locationsTable: UITableView!
    
    var user: User? = nil
    
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
        
        let location = CLLocationCoordinate2D(latitude: 28.60235, longitude: -81.20027)
        
        let span = MKCoordinateSpan(latitudeDelta: 0.01, longitudeDelta: 0.01)
        
        let region = MKCoordinateRegion(center: location, span: span)
        
        mapView?.setRegion(region, animated: true)
        
        
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return locations.count + 1
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        if (indexPath.item < locations.count) {
            let cell = locationsTable.dequeueReusableCell(withIdentifier: "MapCell", for: indexPath) as! MapTableViewCell
            cell.locationName.text = locations[indexPath.item]["name"] as? String
            return cell
        } else {
            let cell = locationsTable.dequeueReusableCell(withIdentifier: "FinishCell", for: indexPath) as! FinishCell
            return cell
        }
    }
    
    
    
    func getLocationData() {
        
        
        
        let params: [String: Any] = [
            "uid": user!.uid,
            "ids": ["4b075beef964a52099fc22e3",
            "4c92a19ed2e1a1439617cc26",
            "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5",
                    "4c92a19ed2e1a1439617cc26",
                    "4d374b67c47db1f7d1a3eea5"]
        ]
        
        let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-foursquare-places")
        var urlRequest = URLRequest(url: url!)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type") // change as per server requirements
        urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
        urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
        if let JSONString = String(data: urlRequest.httpBody!, encoding: String.Encoding.utf8) {
           print(JSONString)
        }
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
                        print("Array count: ", self.locations.count)
                    }
                    
                }
//                if let string = String(data: data, encoding: .utf8) {
//                    print(string)
//                }
                
            }
            
            
        }
        
        task.resume()
        
    }
}
    
