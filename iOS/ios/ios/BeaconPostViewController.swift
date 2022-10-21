import UIKit
import MapKit
import FirebaseAuth
import FirebaseFirestore

extension UIImageView {
    @IBInspectable var borderColor : UIColor? {
        get{
            if let color = layer.borderColor{
                return UIColor(cgColor: color)
            }
            else{
                return nil
            }
        }
        set {layer.borderColor = newValue?.cgColor}
    }
}

@IBDesignable class ScaleSwitch: UISwitch {

    @IBInspectable var scale : CGFloat = 1{
        didSet{
            setup()
        }
    }

    //from storyboard
    required init?(coder aDecoder: NSCoder) {
        super.init(coder: aDecoder)
        setup()
    }
    //from code
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }

    private func setup(){
        self.transform = CGAffineTransform(scaleX: scale, y: scale)
    }

    override func prepareForInterfaceBuilder() {
        setup()
        super.prepareForInterfaceBuilder()
    }


}

// Beacon Title char limit: 16

class BeaconPostViewController: UIViewController, UITableViewDelegate, UITableViewDataSource  {
        
    
    @IBOutlet weak var beaconTitle: UITextView!
    @IBOutlet weak var beaconDescription: UITextView!
    @IBOutlet weak var editButton: UIButton!
    @IBOutlet weak var dateCreated: UILabel!
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var privateSwitch: ScaleSwitch!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    var beaconInfo: [String: Any]!
    var user: User!
    var locations: [[String: Any]] = []
    var ids: [String]!
    var beaconLocation: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            print("No user")
            self.switchToLoggedOut()
        }
        self.locationsTable.delegate = self
        self.locationsTable.dataSource = self
        checkEditing()
        print("ids in beacon post: ", self.ids)
        self.activityIndicator?.isHidden = true
        self.locationsTable.isHidden = true
        getLocationData()
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.beaconTitle.endEditing(true)
        self.beaconDescription.endEditing(true)
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return locations.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = locationsTable.dequeueReusableCell(withIdentifier: "BeaconPostTableCell", for: indexPath) as! BeaconPostTableViewCell
        
        if let description = locations[indexPath.item]["description"] as? String {
            print(description)
            cell.descriptionLabel.text = description
        } else {
            cell.descriptionLabel.text = "There is no listed description for this place."
        }
        print(locations[indexPath.item]["rating"])
        if let rating = locations[indexPath.item]["rating"] as? Double {
            print("Editing rating...")
            if rating / 2 >= 5 {
                cell.ratingLabel.text = "★★★★★"
            } else if rating / 2 >= 4 {
                cell.ratingLabel.text = "★★★★☆"
            } else if rating / 2 >= 3 {
                cell.ratingLabel.text = "★★★☆☆"
            } else if rating / 2 >= 2 {
                cell.ratingLabel.text = "★★☆☆☆"
            } else if rating / 2 >= 1 {
                cell.ratingLabel.text = "★☆☆☆☆"
            } else {
                cell.ratingLabel.text = "Rating: N/A"
            }
            print(cell.ratingLabel)
        } else {
            print("can't make float")
        }
        if let location = locations[indexPath.item]["location"] as? [String: Any] {
            
            if let address = location["formatted_address"] as? String {
                cell.addressLabel.setTitle(address, for: UIControl.State.normal)
            } else {
                cell.addressLabel.setTitle("No address information", for: UIControl.State.normal)
            }
        } else {
            cell.addressLabel.setTitle("No address information", for: UIControl.State.normal)
        }
        if let name = locations[indexPath.item]["name"] as? String {
            cell.nameLabel.text = name
        } else {
            cell.nameLabel.text = "No Name Available"
        }
        return cell
    }
    
    func checkEditing() {
        if (self.isEditing) {
            print("Editing...")
            self.beaconTitle.isEditable = true
            self.beaconDescription.isEditable = true
            self.privateSwitch.isEnabled = true
            self.editButton.setTitle("💾", for: UIControl.State.normal)
        } else {
            self.beaconTitle.isEditable = false
            self.beaconDescription.isEditable = false
            self.privateSwitch.isEnabled = false
            self.editButton.setTitle("✏️", for: UIControl.State.normal)
        }
    }
    
    @IBAction func toggleEditing(_ sender: Any) {
        self.isEditing = !self.isEditing
        
        if (self.isEditing) {
            print("Editing...")
            self.beaconTitle.isEditable = true
            self.beaconDescription.isEditable = true
            self.privateSwitch.isEnabled = true
            self.editButton.setTitle("💾", for: UIControl.State.normal)
        } else {
            self.beaconTitle.isEditable = false
            self.beaconDescription.isEditable = false
            self.privateSwitch.isEnabled = false
            self.editButton.setTitle("✏️", for: UIControl.State.normal)
            saveBeaconData()
        }
        
        
    }
    
    
    
    func saveBeaconData() {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        
        if let info = self.beaconInfo {
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text,
                "intro": self.beaconDescription.text,
                "locations": self.ids,
                "dateUpdated": Date(),
                "dateCreated": info["dateCreated"],
                "numLocations": locations.count,
                "isPrivate": self.privateSwitch.isOn
            ]
        } else {
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text,
                "intro": self.beaconDescription.text,
                "locations": self.ids,
                "beaconLocation": self.beaconLocation,
                "dateUpdated": Date(),
                "dateCreated": Date(),
                "numLocations": locations.count,
                "isPrivate": self.privateSwitch.isOn
            ]
            
            let db = Firestore.firestore()
            
            db.collection("Adventourists")
                .document(self.user.uid)
                .collection("beacons")
                .addDocument(data: beacon) {
                    err in
                    if let err = err {
                        print("Error writing document: \(err)")
                    } else {
                        print("Document successfully written!")
                    }
                }
            
            db.collection("Beacons")
                .addDocument(data: beacon) {
                    err in
                    if let err = err {
                        print("Error writing document: \(err)")
                    } else {
                        print("Document successfully written!")
                    }
                }
        }
        
        
    }
    
    func getLocationData() {
        
        self.activityIndicator?.isHidden = false
        
        let params: [String: Any] = [
            "uid": self.user!.uid,
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
                            print(array)
                            self.locations = array
                            DispatchQueue.main.async {
                                self.locationsTable.reloadData()
                                self.activityIndicator?.stopAnimating()
                                self.locationsTable.isHidden = false
                                print(self.locations.count)
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
    

