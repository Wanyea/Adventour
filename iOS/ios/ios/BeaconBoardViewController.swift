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

class BeaconBoardViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, UISearchBarDelegate {
    
    var listener: ListenerRegistration!
    var nextPageListener: ListenerRegistration!
    
    var beaconLocation = ""
    var lon: Double!
    var lat: Double!
    var hiddenBeacons: [String]!
    
    
    @IBOutlet weak var popUpMenu: UIButton!
    
    // Card Outlets
    private var placesClient: GMSPlacesClient!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var beaconsTable: UITableView!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var errorMessage: UILabel!
    @IBOutlet weak var filter: UIMenu!
    
    var beacons: [[String: Any]] = []
    var user: User!
    var query: Query!
    
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
        self.searchBar.updateHeight(height: 40)
        self.searchBar.searchTextField.textColor = UIColor(named: "adv-royalblue")!
        self.errorMessage.text = "Select a location to see others' Beacons!"
        searchBar?.text = self.beaconLocation
        setUpPopUp()
        
    }
    
    func setUpPopUp() {
        
        self.popUpMenu.menu = UIMenu(children: [
            UIAction(title: "Most Recent", state: .on, handler: { action in
                
            }),
            UIAction(title: "Most Liked", handler: { action in
                
            })
        ])
        
        self.popUpMenu.showsMenuAsPrimaryAction = true
        self.popUpMenu.changesSelectionAsPrimaryAction = true
    }
    
    override func viewWillAppear(_ animated: Bool) {
        getHiddenBeacons()
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
    
    func scrollViewDidEndDragging(_ scrollView: UIScrollView, willDecelerate decelerate: Bool) {
        
        // UITableView only moves in one direction, y axis
        let currentOffset = scrollView.contentOffset.y
        let maximumOffset = scrollView.contentSize.height - scrollView.frame.size.height

        // Change number to adjust the distance from bottom, more negative = further pull
        if maximumOffset - currentOffset <= -40.0 {
            getNextPageBeacons()
        }
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return beacons.count
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let storyboard = UIStoryboard(name: "BeaconPost", bundle: nil)
        if let locations = beacons[indexPath.item]["locations"] as? [[String: Any]] {
            if let vc = storyboard.instantiateViewController(identifier: "BeaconPost") as? BeaconPostViewController {
                vc.locations = locations
                vc.source = self
                vc.beaconInfo = self.beacons[indexPath.item]
                self.navigationController?.pushViewController(vc, animated: true)
            }
            
        }
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = self.beaconsTable.dequeueReusableCell(withIdentifier: "BeaconBoardCell", for: indexPath) as! BeaconBoardTableViewCell
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/YYYY"
        if let firTimestamp = beacons[indexPath.item]["dateCreated"] as? Timestamp {
            let swiftDate = firTimestamp.dateValue()
//            print("Date created: ", data)
            let stringDate = dateFormatter.string(from: swiftDate)
            cell.dateCreated.text = stringDate
        }
        if let title = beacons[indexPath.item]["title"] as? String {
            cell.title.text = title
        }
        if let intro = beacons[indexPath.item]["intro"] as? String {
            cell.intro.text = intro
        }
        getBeaconPicture(beaconInfo: beacons[indexPath.item], cell: cell)
        setNumLikes(beaconInfo: beacons[indexPath.item], cell: cell)
        checkLiked(beaconInfo: beacons[indexPath.item], cell: cell)
        loadUserData(beaconInfo: beacons[indexPath.item], cell: cell)
        return cell
    }
    
    func loadUserData(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell) {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(beaconInfo["uid"] as! String).getDocument { document, error in
            if let document = document, document.exists {
                if let data = document.data() {
                    if let nickname = data["nickname"] as? String {
                        cell.nickname.text = nickname
                    }
                    if let pfpRef = data["iosPfpRef"] as? String {
                        cell.authorProfilePic.image = UIImage(named: pfpRef)
                    }
                }
            }
        }
        
    }
    
    func getBeaconPicture(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell) {
        let sem = DispatchSemaphore(value: 0)
        print("BEACON INFO: \(beaconInfo)")
        if let locations = beaconInfo["locations"] as? [[String: Any]] {
            if locations.count > 0 {
                let location = locations[0]
                if let photos = location["photos"] as? [[String: Any]] {
                    print("photos: \(photos)")
                    for pic in photos {
                        if let prefix = pic["prefix"] as? String {
                            if let suffix = pic["suffix"] as? String {
                                let url = prefix + "original" + suffix
                                cell.beaconPicture.loadFrom(URLAddress: url, semaphore: sem)
                                return
                            }
                        }
                        
                    }
                }
            }
        }
    }
    
    func checkLiked(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell) {
        
        let db = Firestore.firestore()
        var query: Query = db.collection("Likes")
            .whereField("uid", isEqualTo: self.user.uid)
            .whereField("beaconID", isEqualTo: beaconInfo["documentID"])
            
        self.listener = query.addSnapshotListener { snap, error in
            if let error = error {
                print(error)
            }else if snap!.isEmpty {
                cell.likeIcon.image = UIImage(systemName: "heart")
                self.listener.remove()
                
            } else {
                cell.likeIcon.image = UIImage(systemName: "heart.fill")
                self.listener.remove()
            }
        }
    }
    
    func setNumLikes(beaconInfo: [String: Any], cell: BeaconBoardTableViewCell)  {
        let db = Firestore.firestore()
        let beaconRef = db.collection("Beacons").document(beaconInfo["documentID"] as! String)
        getNumLikes(ref: beaconRef, cell: cell)
    }
    
    func getNumLikes(ref: DocumentReference, cell: BeaconBoardTableViewCell) {
        print("Calling getNumLikes...")
        var totalCount: Int = 0
        ref.collection("likeShards").getDocuments() { (querySnapshot, err) in
            
            if err != nil {
                // Error getting shards
                // ...
                return
            } else if !querySnapshot!.isEmpty {
                for document in querySnapshot!.documents {
                    let count = document.data()["likes"] as! Int
                    totalCount += count
                }
            }

            cell.numLikes.text = String(totalCount)
        }
        
    }
    
    
    func getNextPageBeacons() {
        self.nextPageListener = self.query.addSnapshotListener { (snapshot, error) in
            guard let snapshot = snapshot else {
                print("\(error.debugDescription)")
                return
            }

            guard let lastSnapshot = snapshot.documents.last else {
                // The collection is empty.
                return
            }
            print("LAST ", lastSnapshot.documentID)
            var allData: [String: Any]!
            let sem = DispatchSemaphore(value: 0)
            
            let db = Firestore.firestore()
            self.query = db.collection("Beacons")
                .whereField("beaconLocation", isEqualTo: self.beaconLocation)
                .whereField("isPrivate", isEqualTo: false)
                .order(by: "dateCreated", descending: true)
                .limit(to: 10)
                .start(afterDocument: lastSnapshot)
            
            self.query.getDocuments { querySnapshot, err in
                if let err = err {
                    print("Error getting documents: \(err)")
                } else if querySnapshot!.isEmpty {
                    self.beaconsTable?.reloadData()
                    self.activityIndicator.stopAnimating()
                } else {
                    for document in querySnapshot!.documents {
                        
                        if self.hiddenBeacons != nil {
                            print("BEFORE CONTAINS CHECK ", document.documentID)
                            if self.hiddenBeacons.contains(document.documentID) {
                                continue
                            }
                        }
                        
                        print("\(document.documentID) => \(document.data())")
                        let documentID = document.documentID
                        allData = document.data()
                        print(allData)
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
                        self.nextPageListener.remove()
                    }
                }
            }
        }
    }
    
    func getMostRecentBeacons() {
        self.activityIndicator.isHidden = false
        self.errorMessage.isHidden = true
        self.activityIndicator.startAnimating()
        print("GET BEACONS IS RUNNING")
        var allData: [String: Any]!
        self.beacons = []
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        
        self.query = db.collection("Beacons")
            .whereField("beaconLocation", isEqualTo: self.beaconLocation)
            .whereField("isPrivate", isEqualTo: false)
            .order(by: "dateCreated", descending: true)
            .limit(to: 10)
        
        self.query.getDocuments() { (querySnapshot, err) in
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
                                
                                if self.hiddenBeacons != nil {
                                    print("BEFORE CONTAINS CHECK ", document.documentID)
                                    if self.hiddenBeacons.contains(document.documentID) {
                                        continue
                                    }
                                }
                                
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
    
    func getMostLikedBeacons() {
        self.activityIndicator.isHidden = false
        self.errorMessage.isHidden = true
        self.activityIndicator.startAnimating()
        print("GET BEACONS IS RUNNING")
        var allData: [String: Any]!
        self.beacons = []
        let sem = DispatchSemaphore(value: 0)
        
        let db = Firestore.firestore()
        
        self.query = db.collection("Beacons")
            .whereField("beaconLocation", isEqualTo: self.beaconLocation)
            .whereField("isPrivate", isEqualTo: false)
            .limit(to: 10)
        
        self.query.getDocuments() { (querySnapshot, err) in
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
    
    func getHiddenBeacons() {
        print("gethiddenbeacons called")
        let db = Firestore.firestore()
        db.collection("Adventourists")
            .document(self.user.uid)
            .getDocument { snap, error in
                if let error = error {
                    print(error)
                } else {
                    print("check 1")
                    if let data = snap?.data() {
                        if let hidden = data["hiddenBeacons"] as? [String] {
                            print("Hidden beacons ", hidden)
                            self.hiddenBeacons = hidden
                            self.beacons = []
                            print("outside")
                            if self.beaconLocation != "" {
                                self.getMostRecentBeacons()
                                print("inside")
                            }
                        } else {
                            self.hiddenBeacons = []
                            self.beacons = []
                            if self.beaconLocation != "" {
                                self.getMostRecentBeacons()
                            }
                        }
                        
                    } else {
                        self.hiddenBeacons = []
                        self.beacons = []
                        if self.beaconLocation != "" {
                            self.getMostRecentBeacons()
                        }
                    }
                }
            }
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchBar.searchTextField.endEditing(true)
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        searchBar.endEditing(true)
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
      getHiddenBeacons()
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
