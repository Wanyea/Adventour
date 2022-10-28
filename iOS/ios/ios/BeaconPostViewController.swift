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

class BeaconPostViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextViewDelegate {
        
    @IBOutlet weak var beaconTitle: UITextView!
    @IBOutlet weak var beaconIntro: UITextView!
    @IBOutlet weak var editButton: UIButton!
    @IBOutlet weak var dateCreated: UILabel!
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var privateSwitch: ScaleSwitch!
    @IBOutlet weak var locationPhoto1: UIImageView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var nicknameLabel: UILabel!
    
    var source: UIViewController!
    var beaconInfo: [String: Any] = [:]
    var user: User!
    var locations: [[String: Any]] = []
    var ids: [String] = []
    var beaconLocation: String!
    var nickname: String!
    var shouldSave: Bool!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationController?.isNavigationBarHidden = false
        
        
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
        self.activityIndicator?.isHidden = true
        self.locationsTable.isHidden = true
        loadBeaconInfo()
        getLocationData()
        print("nickname on load: ", self.nickname)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.beaconTitle.endEditing(true)
        self.beaconIntro.endEditing(true)
        self.locationsTable.endEditing(true)
    }
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        print("The text view began editing")
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        print("The text view stopped editing.")
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return locations.count
    }
    
    
    func tableView(_ tableView: UITableView, willDisplay cell: UITableViewCell, forRowAt indexPath: IndexPath) {
        if self.isEditing {
            print("Make cell editable")
            if let myCell = cell as? BeaconPostTableViewCell {
                myCell.descriptionTextView.isEditable = true
            }
            
        } else {
            print("Make cell uneditable")
            if let myCell = cell as? BeaconPostTableViewCell {
                myCell.descriptionTextView.isEditable = false
            }
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        
        let cell = locationsTable.dequeueReusableCell(withIdentifier: "BeaconPostTableCell", for: indexPath) as! BeaconPostTableViewCell
        // Load location description
        cell.descriptionTextView.delegate = self
        if let description = locations[indexPath.item]["description"] as? String {
            cell.descriptionTextView.text = description
            
        } else {
            cell.descriptionTextView.text = "There is no listed description for this place."
        }
        if let rating = locations[indexPath.item]["rating"] as? Double {
            cell.cosmosView.rating = rating / 2
        } else {
            cell.cosmosView.rating = 0
        }
        // Load location address
        if let location = locations[indexPath.item]["location"] as? [String: Any] {
            if let address = location["formatted_address"] as? String {
                cell.addressLabel.setTitle(address, for: UIControl.State.normal)
            } else {
                cell.addressLabel.setTitle("No address information", for: UIControl.State.normal)
            }
        } else {
            cell.addressLabel.setTitle("No address information", for: UIControl.State.normal)
        }
        // Load location name
        if let name = locations[indexPath.item]["name"] as? String {
            cell.nameLabel.text = name
        } else {
            cell.nameLabel.text = "No Name Available"
        }
        // Load location photos
        // TODO: Load two addition photos
        if let photos = locations[indexPath.item]["photos"] as? [[String: Any]] {
            if photos.count > 0 {
                let photo = photos[0]
                
                if let prefix = photo["prefix"] as? String {
                    if let suffix = photo["suffix"] as? String {
                        let url = prefix + "original" + suffix
                        cell.locationPhoto1.loadFrom(URLAddress: url)
                    }
                }
                
            }
        }
        
        return cell
    }
    
    func checkEditing() {
        if self.source is BeaconBoardViewController {
            disableEditingBeaconPost()
        } else if (self.isEditing) {
            startEditingBeaconPost()
        } else {
            endEditingBeaconPost()
        }
    }
    
    @IBAction func toggleEditing(_ sender: Any) {
        self.isEditing = !self.isEditing
        
        if (self.isEditing) {
            startEditingBeaconPost()
        } else {
            
            let alert = UIAlertController(
                title: getAlertTitle(),
                message: getAlertMessage(),
                preferredStyle: .actionSheet
            )
            alert.addAction(UIAlertAction(
                title: getButtonTitle(),
                style: .default,
                handler: { _ in
                    self.endEditingBeaconPost()
                    self.saveBeaconData()
                    if self.source is CongratsViewController {
                        self.switchToStart()
                    }
                    
            }))
            alert.addAction(UIAlertAction(
                title: "Cancel",
                style: .cancel,
                handler: { _ in

            }))
            present(alert,
                    animated: true,
                    completion: nil
            )
        }
    }
    
    func saveBeaconData() {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        
        if self.source is ProfileViewController {
            print("Attempting to update beacon info...")
            print("profile beacon title: ", self.beaconTitle.text)
            print("profile nickname: ", self.nickname)
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text!,
                "intro": self.beaconIntro.text!,
                "dateUpdated": Date(),
                "isPrivate": self.privateSwitch.isOn,
            ]
            
            let db = Firestore.firestore()
            print("doc id: ", self.beaconInfo["documentID"] as! String)
            db.collection("Beacons")
                .document(self.beaconInfo["documentID"] as! String)
                .updateData(beacon) {
                    err in
                    if let err = err {
                        print(err)
                    } else {
                        print("Document successfully written!")
                    }
                }
            db.collection("Adventourists")
                .document(self.user.uid)
                .collection("beacons")
                .document(self.beaconInfo["documentID"] as! String)
                .updateData(beacon) {
                    err in
                    if let err = err {
                        print(err)
                    } else {
                        print("Document successfully written!")
                    }
                }
        } else {
            print("beacon title: ", self.beaconTitle.text)
            print("profile nickname: ", self.nickname)
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text,
                "intro": self.beaconIntro.text,
                "locations": self.ids,
                "beaconLocation": self.beaconLocation,
                "dateUpdated": Date(),
                "dateCreated": beaconInfo["dateCreated"],
                "numLocations": locations.count,
                "isPrivate": self.privateSwitch.isOn,
                "author": ["uid": self.user.uid, "nickname": self.nickname]
            ]
            
            let db = Firestore.firestore()
            
            let docRef = db.collection("Adventourists")
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
                .document(docRef.documentID)
                .setData(beacon) {
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
        
        if (ids.isEmpty && locations.isEmpty) {
            self.navigationController?.popToRootViewController(animated: true)
        } else if (!locations.isEmpty) {
            prepareLocationsTable()
            return
        } else {
            
            let params: [String: Any] = [
                "uid": self.user!.uid,
                "ids": self.ids
            ]
            
            let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-foursquare-places")
            var urlRequest = URLRequest(url: url!)
            urlRequest.httpMethod = "POST"
            urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type")
            urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
            urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
            
            let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
                if let data = data {
                    let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                    if let dataDict = dataJsonObject as? [String: Any] {
                        if let array = dataDict["results"] as? [[String: Any]] {
                            print(array)
                            self.locations = array
                            DispatchQueue.main.async {
                                self.prepareLocationsTable()
                                print(self.locations.count)
                            }
                        }
                    }
                }
            }
            task.resume()
        }
    }
    
    func getButtonTitle() -> String {
        if self.source is CongratsViewController {
            return "Post"
        } else {
            return "Save"
        }
    }
    
    func getAlertTitle() -> String {
        if self.source is CongratsViewController {
            return "Post Beacon"
        } else {
            return "Save Beacon"
        }
    }
    
    func getAlertMessage() -> String {
        if self.source is CongratsViewController {
            return "Are you sure you are ready to post your Beacon?"
        } else {
            return "Are you sure you are ready to save your Beacon?"
        }
    }
    
    func disableEditingBeaconPost() {
        self.beaconTitle.isEditable = false
        self.beaconIntro.isEditable = false
        self.privateSwitch.isEnabled = false
        self.editButton.isHidden = true
    }
    
    func endEditingBeaconPost() {
        self.beaconTitle.isEditable = false
        self.beaconIntro.isEditable = false
        self.privateSwitch.isEnabled = false
        self.editButton.isHidden = false
        self.editButton.setTitle("✏️", for: UIControl.State.normal)
        locationsTable.reloadData()
    }
    
    func startEditingBeaconPost() {
        self.beaconTitle.isEditable = true
        self.beaconIntro.isEditable = true
        self.privateSwitch.isEnabled = true
        self.editButton.isHidden = false
        self.editButton.setTitle("💾", for: UIControl.State.normal)
        locationsTable.reloadData()
    }
    
    func prepareLocationsTable() {
        self.locationsTable.reloadData()
        self.activityIndicator?.stopAnimating()
        self.locationsTable.isHidden = false
    }
    
    func loadBeaconInfo() {
        loadDateCreated()
        if self.source is ProfileViewController {
            if let beaconTitle = beaconInfo["title"] as? String {
                self.beaconTitle.text = beaconTitle
            }
            if let beaconIntro = beaconInfo["intro"] as? String {
                self.beaconIntro.text = beaconIntro
            }
            if let isPrivate = beaconInfo["isPrivate"] as? Bool {
                self.privateSwitch.isOn = isPrivate
            }
            if let author = beaconInfo["author"] as? [String: Any] {
                if let nickname = author["nickname"] as? String {
                    self.nicknameLabel.text = nickname
                }
                // TODO: Add profile pic
            }
            // TODO: Load likes
        } else if self.source is CongratsViewController {
            loadUserData()
        }
    }
    
    func loadDateCreated() {
        // Load Date Created info
        
        if self.source is ProfileViewController {
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM/dd/YYYY"
            if let firTimestamp = beaconInfo["dateCreated"] as? Timestamp {
                let swiftDate = firTimestamp.dateValue()
                let stringDate = dateFormatter.string(from: swiftDate)
                self.dateCreated.text = stringDate
            }
        } else if self.source is CongratsViewController {
            // Load Date Created info
            beaconInfo["dateCreated"] = Date()
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM/dd/YYYY"
            
            let stringDate = dateFormatter.string(from: beaconInfo["dateCreated"] as! Date)
            self.dateCreated.text = stringDate
            
        }
        
    }
    
    func loadUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                if let data = document.data() {
                    if let nickname = data["nickname"] as? String {
                        self.nicknameLabel.text = nickname
                        self.nickname = nickname
                    }
                    // TODO: Add profile pic
                    // TODO: Load likes
                }
                
                
            }
        }
        
    }

    
    func switchToStart() {
        
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let mainTabBarController = storyboard.instantiateViewController(identifier: "TabBarViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
    }
    
    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoggedOutViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc, animated: true)
    }
}
