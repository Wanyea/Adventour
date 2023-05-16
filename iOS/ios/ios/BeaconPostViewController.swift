import UIKit
import MapKit
import FirebaseAuth
import FirebaseFirestore

class BeaconPostViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UITextViewDelegate {
    
    let NUM_SHARDS = 10
    
    @IBOutlet weak var beaconTitle: BeaconPostTextView!
    @IBOutlet weak var beaconIntro: BeaconPostTextView!
    @IBOutlet weak var editButton: UIButton!
    @IBOutlet weak var dateCreated: UILabel!
    @IBOutlet weak var locationsTable: UITableView!
    @IBOutlet weak var privateLabel: UILabel!
    @IBOutlet weak var privateSwitch: ScaleSwitch!
    @IBOutlet weak var pfp: UIImageView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var nicknameLabel: UILabel!
    @IBOutlet weak var likeButton: UIButton!
    @IBOutlet weak var numLikes: UILabel!
    @IBOutlet weak var reportHideButton: UIBarButtonItem!
    
   
    var source: UIViewController!
    var beaconInfo: [String: Any] = [:]
    var user: User!
    var locations: [[String: Any]] = []
    var locationDescriptions: [String]!
    var ids: [String] = []
    var beaconLocation: String!
    var nickname: String!
    var shouldSave: Bool!
    var isLiked: Bool = false
    var isBeacon: Bool!
    var listener: ListenerRegistration!

    
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
        checkSource()
        
        //NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(sender:)), name: UIResponder.keyboardWillShowNotification, object: nil);
        //NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(sender:)), name: UIResponder.keyboardWillHideNotification, object: nil);
        
        
        self.likeButton.imageView?.contentMode = .scaleAspectFit
        print("nickname on load: ", self.nickname)
    }
    override func viewDidDisappear(_ animated: Bool) {
        self.dismiss(animated: true, completion: nil)
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        self.beaconTitle.endEditing(true)
        self.beaconIntro.endEditing(true)
        self.locationsTable.endEditing(true)
    }
    
    func textViewDidBeginEditing(_ textView: UITextView) {
        print("The text view began editing")
        if textView is BeaconPostTextView {
            
        }
    }
    
    func textViewDidEndEditing(_ textView: UITextView) {
        print("The text view stopped editing.")
        if let description = textView as? BeaconPostTextView {
            self.locationDescriptions[description.idx] = description.text
            print(self.locationDescriptions)
        }
    }
    
//    @objc func keyboardWillShow(sender: NSNotification) {
//        // Get the size of the keyboard.
//        if let keyboardFrame: NSValue = sender.userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue {
//            let keyboardRectangle = keyboardFrame.cgRectValue
//            let keyboardHeight = keyboardRectangle.height
//
//            print("keyboard ", keyboardFrame.cgRectValue.origin.y, " frame ", self.view.frame.origin.y)
//
//            self.view.frame.origin.y = -keyboardHeight + 50
//        }
//         // Move view 150 points upward
//    }
//
//    @objc func keyboardWillHide(sender: NSNotification) {
//         self.view.frame.origin.y = 0 // Move view to original position
//    }
    
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
        cell.descriptionTextView.idx = indexPath.item
        if self.isEditing {
            cell.descriptionTextView.layer.borderWidth = 1
            cell.descriptionTextView.layer.borderColor = UIColor.systemGray5.cgColor
            cell.descriptionTextView.layer.backgroundColor = UIColor.systemGray6.cgColor
            cell.descriptionTextView.layer.cornerRadius = 7
        } else {
            cell.descriptionTextView.layer.borderWidth = 0
            cell.descriptionTextView.layer.borderColor = UIColor.white.cgColor
            cell.descriptionTextView.layer.backgroundColor = UIColor.white.cgColor
            cell.descriptionTextView.layer.cornerRadius = 0
        }
        if let locationDescriptions = self.locationDescriptions {
            
            if  locationDescriptions[indexPath.item] != "" {
                cell.descriptionTextView.text = locationDescriptions[indexPath.item]
            } else {
                if let description = locations[indexPath.item]["description"] as? String {
                    cell.descriptionTextView.text = description
                    
                } else {
                    cell.descriptionTextView.text = "There is no listed description for this place."
                }
            }
        } else if let locationDescriptions = beaconInfo["locationDescriptions"] as? [String] {
            
            if  locationDescriptions[indexPath.item] != "" {
                cell.descriptionTextView.text = locationDescriptions[indexPath.item]
            } else {
                if let description = locations[indexPath.item]["description"] as? String {
                    cell.descriptionTextView.text = description
                    
                } else {
                    cell.descriptionTextView.text = "There is no listed description for this place."
                }
            }
        } else {
            if let description = locations[indexPath.item]["description"] as? String {
                cell.descriptionTextView.text = description
                
            } else {
                cell.descriptionTextView.text = "There is no listed description for this place."
            }
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
        let sem = DispatchSemaphore(value: 0)
        if let photos = locations[indexPath.item]["photos"] as? [[String: Any]] {
            if photos.count > 0 {
                let photo = photos[0]
                
                if let prefix = photo["prefix"] as? String {
                    if let suffix = photo["suffix"] as? String {
                        let url = prefix + "original" + suffix
                        cell.locationPhoto1.loadFrom(URLAddress: url, semaphore: sem)
                    }
                }
                
            }
            if photos.count > 1 {
                let photo = photos[1]
                
                if let prefix = photo["prefix"] as? String {
                    if let suffix = photo["suffix"] as? String {
                        let url = prefix + "original" + suffix
                        cell.locationPhoto2.loadFrom(URLAddress: url, semaphore: sem)
                    }
                }
                
            }
            if photos.count > 2 {
                let photo = photos[2]
                
                if let prefix = photo["prefix"] as? String {
                    if let suffix = photo["suffix"] as? String {
                        let url = prefix + "original" + suffix
                        cell.locationPhoto3.loadFrom(URLAddress: url, semaphore: sem)
                    }
                }
                
            }
            
        }
        sem.wait()
        return cell
    }
    
    @IBAction func reportTapped(_ sender: Any) {
        let alert = UIAlertController(
            title: "Report/Hide Beacon",
            message: "Would you like to Report or Hide this Beacon post?",
            preferredStyle: .actionSheet
        )
        
        alert.addAction(UIAlertAction(
            title: "Report",
            style: .destructive,
            handler: { _ in
                let alert = UIAlertController(
                    title: "Report Beacon",
                    message: "If you believe content in this Beacon breaks our Terms of Service please press \"Report\". A member of the Adventour team will review your report. If this content is found to break our Terms of Service it will be removed promptly.\n\nThank you for keeping Adventour a safe place for everyone!",
                    preferredStyle: .actionSheet
                )
                alert.addAction(UIAlertAction(
                    title: "Report",
                    style: .destructive,
                    handler: { _ in
                        self.reportBeaconPost()
                        
                }))
                alert.addAction(UIAlertAction(
                    title: "Cancel",
                    style: .cancel,
                    handler: { _ in

                }))
                self.present(alert,
                        animated: true,
                        completion: nil
                )
                
        }))
        alert.addAction(UIAlertAction(
            title: "Hide",
            style: .destructive,
            handler: { _ in
                let alert = UIAlertController(
                    title: "Hide Beacon",
                    message: "If this type of content is not something you wish to see tap Hide.",
                    preferredStyle: .actionSheet
                )
                alert.addAction(UIAlertAction(
                    title: "Hide",
                    style: .destructive,
                    handler: { _ in
                        self.hideBeaconPost(withDocumentID: self.beaconInfo["documentID"] as! String)
                }))
                alert.addAction(UIAlertAction(
                    title: "Cancel",
                    style: .cancel,
                    handler: { _ in

                }))
                self.present(alert,
                        animated: true,
                        completion: nil
                )
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
    
    
    @IBAction func likeTapped(_ sender: Any) {
        self.isLiked = !self.isLiked
        print("LIKE BUTTON TAPPED!")
        if self.isLiked {
            self.likeButton.setImage(UIImage(systemName: "heart.fill"), for: UIControl.State.normal)
            saveLike()
        } else {
            self.likeButton.setImage(UIImage(systemName: "heart"), for: UIControl.State.normal)
            deleteLike()
        }
    }
    
    func saveLike() {
        print("DOC ID: ", beaconInfo["documentID"])
        let db = Firestore.firestore()
        var query: Query? = db.collection("Likes")
            .whereField("uid", isEqualTo: self.user.uid)
            .whereField("beaconID", isEqualTo: beaconInfo["documentID"])
        query!.getDocuments { snap, error in
            if let error = error {
                        print("Error getting documents: \(error)")
            } else if snap!.isEmpty {
                db.collection("Likes")
                    .addDocument(data: [
                        "beaconID": self.beaconInfo["documentID"],
                        "uid": self.user.uid
                    ])
                let beaconRef = db.collection("Beacons")
                    .document(self.beaconInfo["documentID"] as! String)
                print("PREPARING TO INCREMENT")
                self.incrementLikesCounter(ref: beaconRef, numShards: self.NUM_SHARDS)
                self.setNumLikes()
            }
        }
        
    }
    
    func deleteLike() {
        let db = Firestore.firestore()
        var query: Query = db.collection("Likes")
            .whereField("uid", isEqualTo: self.user.uid)
            .whereField("beaconID", isEqualTo: beaconInfo["documentID"])
        query.getDocuments { snap, error in
            if let error = error {
                print("Error getting documents: \(error)")
            } else {
                for document in snap!.documents {
                    db.collection("Likes")
                        .document(document.documentID)
                        .delete()
                    let beaconRef = db.collection("Beacons")
                        .document(self.beaconInfo["documentID"] as! String)
                    print("PREPARING TO DECREMENT")
                    self.decrementLikesCounter(ref: beaconRef, numShards: self.NUM_SHARDS)
                    self.setNumLikes()
                }
            }
        }
    }
    
    func checkLiked() {
        
        let db = Firestore.firestore()
        var query: Query = db.collection("Likes")
            .whereField("uid", isEqualTo: self.user.uid)
            .whereField("beaconID", isEqualTo: beaconInfo["documentID"])
            
        self.listener = query.addSnapshotListener { snap, error in
            if snap!.isEmpty {
                self.isLiked = false
                self.likeButton.setImage(UIImage(systemName: "heart"), for: UIControl.State.normal)
                self.listener.remove()
            } else {
                self.isLiked = true
                self.likeButton.setImage(UIImage(systemName: "heart.fill"), for: UIControl.State.normal)
                self.listener.remove()
            }
            
        }
    }
    
    func setNumLikes()  {
        let db = Firestore.firestore()
        let beaconRef = db.collection("Beacons").document(self.beaconInfo["documentID"] as! String)
        getNumLikes(ref: beaconRef)
    }
    
    func getNumLikes(ref: DocumentReference) {
        print("Calling getNumLikes...")
        var totalCount: Int = 0
        ref.collection("likeShards").getDocuments() { (querySnapshot, err) in
            
            if err != nil {
                // Error getting shards
                // ...
                return
            } else {
                for document in querySnapshot!.documents {
                    let count = document.data()["likes"] as! Int
                    totalCount += count
                }
            }

            self.numLikes.text = String(totalCount)
        }
        
    }
    
    func incrementLikesCounter(ref: DocumentReference, numShards: Int) {
        // Select a shard of the counter at random
        let shardId = Int(arc4random_uniform(UInt32(numShards)))
        let shardRef = ref.collection("likeShards").document(String(shardId))

        shardRef.setData([
            "likes": FieldValue.increment(Int64(1))
        ], merge: true)
    }
    
    func decrementLikesCounter(ref: DocumentReference, numShards: Int) {
        // Select a shard of the counter at random
        let shardId = Int(arc4random_uniform(UInt32(numShards)))
        let shardRef = ref.collection("likeShards").document(String(shardId))

        shardRef.setData([
            "likes": FieldValue.increment(Int64(-1))
        ], merge: true)
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
                    self.navigationController?.isNavigationBarHidden = false
                    self.tabBarController?.tabBar.isHidden = false
                    self.tabBarController?.tabBar.isTranslucent = false
                    if self.source is CongratsViewController {
                        self.performSegue(withIdentifier: "goHome", sender: self)
                    } else if self.source is AdventourSummaryViewController {
                        self.navigationController?.popToRootViewController(animated: true)
                        self.performSegue(withIdentifier: "goHome", sender: self)
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
    
    func checkSource() {
        if self.source is BeaconBoardViewController {
            checkLiked()
            setNumLikes()
            self.reportHideButton.isEnabled = true
            self.reportHideButton.tintColor = UIColor(named: "adv-red")
            
            
        } else if self.source is ProfileViewController {
            checkLiked()
            setNumLikes()
            self.reportHideButton.isEnabled = false
            self.reportHideButton.tintColor = UIColor(named: "adv-skyblue")
            
        } else if self.source is AdventourSummaryViewController {
            self.likeButton.isHidden = true
            self.numLikes.isHidden = true
            self.reportHideButton.isEnabled = false
            self.reportHideButton.tintColor = UIColor(named: "adv-skyblue")
        } else {
            self.likeButton.isEnabled = false
            self.numLikes.isHidden = true
            self.reportHideButton.isEnabled = false
            self.reportHideButton.tintColor = UIColor(named: "adv-skyblue")
        }
    }
    
    func saveBeaconData() {
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        print("DESCRIPTIONS: ", self.locationDescriptions)
        if self.source is ProfileViewController {
            print("Attempting to update beacon info...")
            print("profile beacon title: ", self.beaconTitle.text)
            print("profile nickname: ", self.nickname)
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text!,
                "intro": self.beaconIntro.text!,
                "locationDescriptions": self.locationDescriptions,
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
            db.collection("Adventourists")
                .document(self.user.uid)
                .collection("adventours")
                .document(beaconInfo["documentID"] as! String)
                .setData(["isBeacon": true], merge: true)
            
            self.locationsTable.reloadData()
        } else {
            print("beacon title: ", self.beaconTitle.text)
            print("profile nickname: ", self.nickname)
            let beacon: [String: Any] = [
                "title": self.beaconTitle.text,
                "intro": self.beaconIntro.text,
                "locations": self.ids,
                "locationDescriptions": self.locationDescriptions,
                "beaconLocation": self.beaconLocation,
                "dateUpdated": Date(),
                "dateCreated": beaconInfo["dateCreated"],
                "numLocations": locations.count,
                "isPrivate": self.privateSwitch.isOn,
                "uid": self.user.uid
            ]
            
            let db = Firestore.firestore()
            
            db.collection("Adventourists")
                .document(self.user.uid)
                .collection("beacons")
                .document(beaconInfo["documentID"] as! String)
                .setData(beacon, merge: true) {
                    err in
                    if let err = err {
                        print("Error writing document: \(err)")
                    } else {
                        print("Document successfully written!")
                    }
                }
            
            db.collection("Adventourists")
                .document(self.user.uid)
                .collection("adventours")
                .document(beaconInfo["documentID"] as! String)
                .setData(["isBeacon": true], merge: true)
            
            let beaconRef = db.collection("Beacons")
                .document(beaconInfo["documentID"] as! String)
            
                beaconRef.setData(beacon) {
                    err in
                    if let err = err {
                        print("Error writing document: \(err)")
                    } else {
                        print("Document successfully written!")
                    }
                }
            createLikesCounter(ref: beaconRef, numShards: self.NUM_SHARDS)
            self.locationsTable.reloadData()
        }
        
        
    }
    
    func createLikesCounter(ref: DocumentReference, numShards: Int) {
        ref.setData(["numLikeShards": numShards], merge: true){ (err) in
            for i in 0...numShards-1 {
                ref.collection("likeShards").document(String(i)).setData(["likes": 0])
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
        if self.source is CongratsViewController || self.source is AdventourSummaryViewController {
            return "Post Beacon"
        } else {
            return "Save Beacon"
        }
    }
    
    func getAlertMessage() -> String {
        if self.source is CongratsViewController || self.source is AdventourSummaryViewController {
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
        self.beaconTitle.layer.borderWidth = 0
        self.beaconTitle.layer.borderColor = UIColor.white.cgColor
        self.beaconTitle.layer.backgroundColor = UIColor.white.cgColor
        self.beaconTitle.layer.cornerRadius = 0
        
        self.beaconIntro.isEditable = false
        self.beaconIntro.layer.borderWidth = 0
        self.beaconIntro.layer.borderColor = UIColor.white.cgColor
        self.beaconIntro.layer.backgroundColor = UIColor.white.cgColor
        self.beaconIntro.layer.cornerRadius = 0
        
        self.privateSwitch.isEnabled = false
        self.editButton.isHidden = false
        self.editButton.setTitle("✏️", for: UIControl.State.normal)
        locationsTable.reloadData()
    }
    
    func startEditingBeaconPost() {
        self.beaconTitle.isEditable = true

        self.beaconTitle.layer.borderWidth = 1
        self.beaconTitle.layer.borderColor = UIColor.systemGray5.cgColor
        self.beaconTitle.layer.backgroundColor = UIColor.systemGray6.cgColor
        self.beaconTitle.layer.cornerRadius = 7
        
        self.beaconIntro.isEditable = true
        self.beaconIntro.layer.borderWidth = 1
        self.beaconIntro.layer.borderColor = UIColor.systemGray5.cgColor
        self.beaconIntro.layer.backgroundColor = UIColor.systemGray6.cgColor
        self.beaconIntro.layer.cornerRadius = 7
        
        self.privateSwitch.isEnabled = true
        self.editButton.isHidden = false
        self.editButton.setTitle("💾", for: UIControl.State.normal)
        locationsTable.reloadData()
    }
    
    func prepareLocationsTable() {
        if self.locationDescriptions != nil {
            print("ALREADY GOT DEM DESCRIPTIONS")
        }
        else if let locationDescriptions = beaconInfo["locationDescriptions"] as? [String] {
            self.locationDescriptions = locationDescriptions
        } else {
            self.locationDescriptions = Array(repeating: "", count: self.locations.count)
        }
        print(self.locationDescriptions)
        self.locationsTable.reloadData()
        self.activityIndicator?.stopAnimating()
        self.locationsTable.isHidden = false
    }
    
    func loadBeaconInfo() {
        // Set up date formatter
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        
        if self.source is BeaconBoardViewController {
            self.privateSwitch.isHidden = true
            self.privateLabel.isHidden = true
        } else {
            self.privateSwitch.isHidden = false
            self.privateLabel.isHidden = false
        }
        
        if self.source is ProfileViewController || self.source is BeaconBoardViewController {
            if let firTimestamp = beaconInfo["dateCreated"] as? Timestamp {
                let swiftDate = firTimestamp.dateValue()
                let stringDate = dateFormatter.string(from: swiftDate)
                self.dateCreated.text = stringDate
            }
            if let beaconTitle = beaconInfo["title"] as? String {
                self.beaconTitle.text = beaconTitle
            }
            if let beaconIntro = beaconInfo["intro"] as? String {
                self.beaconIntro.text = beaconIntro
            }
            if let isPrivate = beaconInfo["isPrivate"] as? Bool {
                self.privateSwitch.isOn = isPrivate
            }
            loadUserData(uid: beaconInfo["uid"] as! String)
            // TODO: Load likes
        } else if self.source is CongratsViewController || self.source is AdventourSummaryViewController {
            loadUserData(uid: self.user.uid)
            beaconInfo["dateCreated"] = Date()
            let dateFormatter = DateFormatter()
            dateFormatter.dateFormat = "MM/dd/YYYY"
            
            let stringDate = dateFormatter.string(from: beaconInfo["dateCreated"] as! Date)
            self.dateCreated.text = stringDate
        }
    }
    
    func loadUserData(uid: String) {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(uid).getDocument { document, error in
            if let document = document, document.exists {
                if let data = document.data() {
                    if let nickname = data["nickname"] as? String {
                        self.nicknameLabel.text = nickname
                        self.nickname = nickname
                    }
                    if let pfpRef = data["iosPfpRef"] as? String {
                        self.pfp.image = UIImage(named: pfpRef)
                    }

                }
                
                
            }
        }
        
    }

    func reportBeaconPost() {
        
        let params: [String: Any] = [
            "uid": beaconInfo["uid"] as! String,
            "id": beaconInfo["documentID"]
        ]
        
        let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/send-email-report")
        var urlRequest = URLRequest(url: url!)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type") // change as per server requirements
        urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
        urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
        
        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            if let data = data {
                let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                if let dataDict = dataJsonObject as? [String: Any] {
                        print(dataDict)
                }
            }
        }
        task.resume()
        self.hideBeaconPost(withDocumentID: beaconInfo["documentID"] as! String)
    }
    
    func hideBeaconPost(withDocumentID documentID: String) {
        let db = Firestore.firestore()
        
        var hiddenBeacons: [String]!
        
        db.collection("Adventourists")
            .document(self.user.uid)
            .getDocument { snap, error in
                if let error = error {
                    print(error)
                } else {
                    print("Made it in else")
                    if let data = snap?.data() {
                        print("data exists")
                        if let hidden = data["hiddenBeacons"] as? [String] {
                            hiddenBeacons = hidden
                            hiddenBeacons.append(documentID)
                            db.collection("Adventourists")
                                .document(self.user.uid)
                                .updateData([
                                    "hiddenBeacons": hiddenBeacons
                                ]) { error in
                                    self.navigationController?.popToRootViewController(animated: true)
                                }
                            
                        } else {
                            hiddenBeacons = []
                            hiddenBeacons.append(documentID)
                            db.collection("Adventourists")
                                .document(self.user.uid)
                                .updateData([
                                    "hiddenBeacons": hiddenBeacons
                                ]) { error in
                                    self.navigationController?.popToRootViewController(animated: true)
                                }
                        }
                    } else {
                        hiddenBeacons = []
                        hiddenBeacons.append(documentID)
                        db.collection("Adventourists")
                            .document(self.user.uid)
                            .updateData([
                                "hiddenBeacons": hiddenBeacons
                            ]) { error in
                                self.navigationController?.popToRootViewController(animated: true)
                            }
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

extension UITextView {
    @IBInspectable var borderColor: UIColor? {
        get {
            if let color = layer.borderColor{
                return UIColor(cgColor: color)
            } else{
                return nil
            }
        }
        
        set {
            layer.borderColor = newValue?.cgColor
        }
    }
    
    @IBInspectable var borderWidth: CGFloat {
        get {
            return layer.borderWidth
        }
        
        set {
            layer.borderWidth = newValue
        }
    }
    
    @IBInspectable var cornerRadius: CGFloat {
        get {
            return layer.cornerRadius
        }
        
        set {
            layer.cornerRadius = newValue
        }
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
