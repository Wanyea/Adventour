import UIKit
import MapKit
import FirebaseAuth
import GooglePlaces


class StartViewController: UIViewController, UISearchBarDelegate {
    
    var beaconLocation = ""
    var lon: Double!
    var lat: Double!
    var excludes: [String] = []
    var user: User!
    
    // Filter Outlets

    var socialSwitch: UISwitch!
    var outdoorsySwitch: UISwitch!
    var cultureSwitch: UISwitch!
    var hungrySwitch: UISwitch!
    var romanticSwitch: UISwitch!
    var geekySwitch: UISwitch!
    var spirtualSwitch: UISwitch!
    var sportySwitch: UISwitch!
    var chillSwitch: UISwitch!
    var shoppySwitch: UISwitch!
    var pamperedSwitch: UISwitch!
    var distanceSlider: UISlider!
    
    // Card Outlets
    private var placesClient: GMSPlacesClient!
    @IBOutlet weak var searchBar: UISearchBar!
    @IBOutlet weak var locationPhoto: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var websiteClickable: UILabel!
    @IBOutlet weak var phoneClickable: UILabel!
    @IBOutlet weak var cosmosView: CosmosView!
    
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    
    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var websiteLabel: UILabel!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var errorMessageLabel: UILabel!
    
    // Button Outlets
    @IBOutlet weak var notNow: UIButton!
    
    var fsq_id: String!
    var ids: [String]? = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
               
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            self.switchToLoggedOut()
        }
        self.websiteLabel?.adjustsFontSizeToFitWidth = true
        self.websiteLabel?.minimumScaleFactor = 0.75
        self.websiteLabel?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.websiteTapped)))
        self.phoneLabel?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.phoneTapped)))
        self.cosmosView.settings.fillMode = .precise
        
        self.searchBar.updateHeight(height: 55)
        self.searchBar.searchTextField.textColor = UIColor(named: "adv-royalblue")!
        //Adding the bordercolor and corner radius on the not now button. Actual border is in its runtime attributes.
        
        // Do any additional setup after loading the view.
        placesClient = GMSPlacesClient.shared()
        self.searchBar?.text = self.beaconLocation
        self.searchBar?.delegate = self
    }
    
    @IBAction func getCurrentPlace(_ sender: Any){
        let autocompleteController = GMSAutocompleteViewController()
        autocompleteController.delegate = self
        let fields: GMSPlaceField = GMSPlaceField(rawValue: UInt(GMSPlaceField.coordinate.rawValue) |  UInt(GMSPlaceField.name.rawValue) | UInt(GMSPlaceField.formattedAddress.rawValue) | UInt(GMSPlaceField.placeID.rawValue) | UInt(GMSPlaceField.addressComponents.rawValue))
        
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
    
    override func viewWillAppear(_ animated: Bool) {
        self.errorMessageLabel?.isHidden = true
//        self.websiteClickable?.adjustsFontSizeToFitWidth = true
//        self.websiteClickable?.minimumScaleFactor = 0.75
        self.websiteClickable?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.websiteTapped)))
        self.phoneClickable?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.phoneTapped)))
        self.notNow?.isEnabled = false
        self.notNow?.layer.borderColor = UIColor.gray.cgColor
        self.notNow?.layer.shadowColor = UIColor.gray.cgColor
        self.notNow?.layer.borderWidth = 5
        self.notNow?.layer.cornerRadius = 25
        
        self.fsq_id = nil
        if let ids = self.ids {
            print("Start, These is the ids: ", ids)
        } else {
            print("Start, These ids is nil")
        }
        hideCardInfo()
    }
    
    func searchBarCancelButtonClicked(_ searchBar: UISearchBar) {
        searchBar.searchTextField.endEditing(true)
    }
    
    func searchBarTextDidBeginEditing(_ searchBar: UISearchBar) {
        self.lat = nil
        self.lon = nil
        searchBar.endEditing(true)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? MapViewController {
            preparePlace()
            destinationVC.ids = self.ids
            destinationVC.beaconLocation = self.beaconLocation
        }
    }
    
    @IBAction func goTapped(_ sender: Any) {
        self.notNow.isEnabled = true
        self.notNow?.layer.borderColor = UIColor(named: "adv-red")?.cgColor
        self.notNow?.layer.shadowColor = UIColor(named: "adv-redshade")?.cgColor
        getAdventourPlace()
    }
    
    @IBAction func notNowTapped(_ sender: Any) {
        excludes.append(self.fsq_id)
        getAdventourPlace()
    }
    
    @IBAction func yesTapped(_ sender: Any) {
        excludes.append(self.fsq_id)
    }
    
    func getAdventourPlace() {
        self.hideCardInfo()
        self.activityIndicator?.isHidden = false
        self.activityIndicator?.startAnimating()
        getAdventourLocation()
    }
    
    @IBAction func goHome(sender: UIStoryboardSegue){
        if sender.source is CongratsViewController {
            self.ids = []
            self.excludes = []
            self.searchBar.text = ""
            self.lat = nil
            self.lon = nil
        }
    }
    
    
    func preparePlace() {
        if self.fsq_id == nil {
            return
        }
        self.ids?.insert(self.fsq_id, at: 0)
    }
    
    @objc func websiteTapped() {
        if let string = websiteClickable.text {
            if let url = URL(string: string) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
    }
    
    @objc func phoneTapped() {
        if let string = self.phoneClickable?.text {
            let phone = string.replacingOccurrences(of: " ", with: "").replacingOccurrences(of: "(", with: "").replacingOccurrences(of: ")", with: "").replacingOccurrences(of: "-", with: "")
            if let url = URL(string: "tel://" + phone) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
    }
        

    @IBAction func getAdventourLocation() {
        
        self.errorMessageLabel.isHidden = true
        var latlonString: String
        if self.lat != nil && self.lon != nil {
            latlonString = String(self.lat) + "," + String(self.lon)
        } else {
            self.errorMessageLabel.isHidden = false
            self.errorMessageLabel.text = "Please select a location to start an Adventour!"
            self.activityIndicator.stopAnimating()
            return
        }

        let params: [String: Any] = [
            "uid": self.user.uid,
            "ll": latlonString,
            "radius": self.milesToMeters(distanceInMiles: self.distanceSlider?.value ?? 5),
            "categories": getCategoryString(),
            "excludes": excludes
        ]
        print(params)
        let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-adventour-place")
        var urlRequest = URLRequest(url: url!)
        urlRequest.httpMethod = "POST"
        urlRequest.addValue("application/json", forHTTPHeaderField: "Content-Type") // change as per server requirements
        urlRequest.addValue("application/json", forHTTPHeaderField: "Accept")
        urlRequest.httpBody = try! JSONSerialization.data(withJSONObject: params, options: [])
        
        var sem = DispatchSemaphore(value: 0)
        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            // do stuff
            
            if let data = data {
                let dataJsonObject = try? JSONSerialization.jsonObject(with: data, options: [])
                if let dataDict = dataJsonObject as? [String: Any] {
                    if let data = dataDict["data"] as? [String: Any] {
                        print("+=================+")
                        print("| Adventour Place |")
                        print("+=================+")
                        print(data)
                        DispatchQueue.main.async {
                            
                            if let fsq_id = data["fsq_id"] as? String {
                                self.fsq_id = fsq_id
                            } else {
                                print("There was no fsq_id to be found...")
                                self.hideCardInfo()
                            }
                            
                            if let name = data["name"] as? String {
                                self.nameLabel?.text = name
                            } else {
                                self.nameLabel?.text = "This place does not have a name."
                            }
                            
                            if let description = data["description"] as? String {
                                self.descriptionLabel?.text = description
                            } else {
                                self.descriptionLabel?.text = "This place does not have a listed description."
                            }
                            
                            if let tel = data["tel"] as? String {
                                self.phoneClickable?.text = tel
                            } else {
                                self.phoneClickable?.text = "N/A"
                            }
                            
                            if let website = data["website"] as? String {
                                self.websiteClickable?.text = website
                            } else {
                                self.websiteClickable?.text = "N/A"
                            }
                            if let rating = data["rating"] as? Double {
                                self.cosmosView.rating = rating / 2
                            } else {
                                self.cosmosView.rating = 0
                                self.cosmosView.text = "No rating available"
                            }
                            if let distance = data["distance"] as? Double {
                                self.distanceLabel.text = String(self.metersToMilesRounded(distanceInMeters: distance)) + " miles away"
                            } else {
                                self.distanceLabel.text = "Unable to determine distance"
                            }
                            if let photos = data["photos"] as? [[String: Any]] {
                                if photos.count > 0 {
                                    let photo = photos[0]
                                    
                                    if let prefix = photo["prefix"] as? String {
                                        if let suffix = photo["suffix"] as? String {
                                            let url = prefix + "original" + suffix
                                            self.locationPhoto.loadFrom(URLAddress: url, semaphore: sem)
                                        }
                                    }
                                    
                                }
                            }
                            sem.wait()
                            DispatchQueue.main.async {
                                self.activityIndicator?.stopAnimating()
                                self.showCardInfo()
                            }
                        }
                        
                    } else {
                        DispatchQueue.main.async {
                            self.errorMessageLabel.text = "The search was unable to return a place. Please check your search filters and try again."
                            self.errorMessageLabel.isHidden = false
                            self.activityIndicator.stopAnimating()
                        }
                    }
                    
                }
            }
        }
        task.resume()
    }
    
    func getCategoryString() -> String {
        var categories: String = ""
        if UserDefaults.standard.bool(forKey: "socialSwitch") {
            let socialString = "10001,10003,10006,10007,10009,10017,10019,10022,10023,10039,10048,10055,10056,12004,14003,14009,10015,"
            categories.append(socialString)
        }
        if UserDefaults.standard.bool(forKey: "outdoorsySwitch"){
            let outdoorsyString = "10014,10044,10055,10056,16002,16003,16004,16005,16006,16008,16009,16011,16012,16013,16016,16017,16018,16019,16021,16022,16023,16024,16027,16028,16032,16043,16044,16046,16048,16049,16051,16052,19002,19003,19008,19021,"
            categories.append(outdoorsyString)
        }
        if UserDefaults.standard.bool(forKey: "cultureSwitch") {
            let culturedString = "10002,10004,10016,10024,10028,10031,10030,10042,17002,17003,10043,17018,10047,10056,11005,17098,17113,11140,12005,12065,12066,12080,12081,16011,16024,16007,16020,16025,16026,16031,17103,16047,"
            categories.append(culturedString)
        }
        if UserDefaults.standard.bool(forKey: "hungrySwitch") {
            let hungryString = "13028,13053,13054,13065,13001,13002,13032,13040,13059,13381,13382,"
            categories.append(hungryString)
        }
        if UserDefaults.standard.bool(forKey: "romanticSwitch") {
            let romanticString = "10004,10016,10024,10023,11140,16005,11073,"
            categories.append(romanticString)
        }
        if UserDefaults.standard.bool(forKey: "geekySwitch") {
            let geekyString = "10003,10015,10018,17018,17022,17091,17027,17135,10044,10054,12080,12081,"
            categories.append(geekyString)
        }
        if UserDefaults.standard.bool(forKey: "spirtualSwitch") {
            let spirtualString = "12098,"
            categories.append(spirtualString)
        }
        if UserDefaults.standard.bool(forKey: "sportySwitch") {
            let sportyString = "10006,10014,10019,10022,10023,10045,10048,18005,18008,18012,18019,18020,18021,18029,18034,17117,18035,18036,18037,18039,18040,18048,18054,18057,18058,18064,18067,19002,"
            categories.append(sportyString)
        }
        if UserDefaults.standard.bool(forKey: "chillSwitch") {
            let chillString = "10003,10006,10015,10020,10024,10025,10045,10056,11005,11073,12080,12081,19021,16003,16005,16032,"
            categories.append(chillString)
        }
        if UserDefaults.standard.bool(forKey: "shoppySwitch") {
            let shoppyString = "14009,17002,17002,17004,17018,17020,17022,17027,17024,17030,17031,17032,17039,17053,17054,17055,17056,17089,17091,17098,17107,17111,17113,17116,17117,17135,17138,17103,"
            categories.append(shoppyString)
        }
        if UserDefaults.standard.bool(forKey: "pamperedSwitch") {
            let pamperedString = "11062,11063,11064,11071,11072,11073,11074,11070,17030,11140,15001,17020,"
            categories.append(pamperedString)
        }
        print(categories)
        return categories
    }

    func milesToMeters(distanceInMiles distance: Float) -> Int {
        return Int(distance * 1609.344)
    }
    
    func metersToMilesRounded(distanceInMeters distance: Double) -> Double {
        print("Distance in meters: ", distance)
        return round(Double(distance / 1609.344) * 10) / 10.0
    }
    
    func hideCardInfo() {
        self.locationPhoto?.isHidden = true
        self.nameLabel?.isHidden = true
        self.distanceLabel?.isHidden = true
        self.descriptionLabel?.isHidden = true
        self.websiteClickable?.isHidden = true
        self.phoneClickable?.isHidden = true
        self.cosmosView.isHidden = true
        
        self.phoneLabel?.isHidden = true
        self.websiteLabel?.isHidden = true
        self.activityIndicator?.isHidden = true
    }
    
    func showCardInfo() {
        self.locationPhoto?.isHidden = false
        self.nameLabel?.isHidden = false
        self.distanceLabel?.isHidden = false
        self.descriptionLabel?.isHidden = false
        self.websiteClickable?.isHidden = false
        self.phoneClickable?.isHidden = false
        self.cosmosView.isHidden = false

        
        self.phoneLabel?.isHidden = false
        self.websiteLabel?.isHidden = false
    }
    
    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoggedOutViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc)
    }
    
}

extension UIImageView {
    func loadFrom(URLAddress: String, semaphore: DispatchSemaphore) {
        guard let url = URL(string: URLAddress) else {
            return
        }
        let urlRequest = URLRequest(url: url)
        let task = URLSession.shared.dataTask(with: urlRequest) { data, response, error in
            defer {semaphore.signal()}
            DispatchQueue.main.async { [weak self] in
                if let imageData = data {
                    if let loadedImage = UIImage(data: imageData) {
                            self?.image = loadedImage
                    }
                }
            }
        }
        task.resume()
        
    }
}

extension StartViewController: GMSAutocompleteViewControllerDelegate {

    
    
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

extension UISearchBar {
    func updateHeight(height: CGFloat, radius: CGFloat = 8.0) {
        let image: UIImage? = UIImage.imageWithColor(color: UIColor(named: "adv-royalblue")!, size: CGSize(width: 1, height: height))
        setSearchFieldBackgroundImage(image, for: .normal)
        for subview in self.subviews {
            for subSubViews in subview.subviews {
                if #available(iOS 13.0, *) {
                    for child in subSubViews.subviews {
                        if let textField = child as? UISearchTextField {
                            textField.layer.cornerRadius = radius
                            textField.clipsToBounds = true
                        }
                    }
                    continue
                }
                if let textField = subSubViews as? UITextField {
                    textField.layer.cornerRadius = radius
                    textField.clipsToBounds = true
                }
            }
        }
    }
}

private extension UIImage {
    static func imageWithColor(color: UIColor, size: CGSize) -> UIImage? {
        let rect = CGRect(x: 0, y: 0, width: size.width, height: size.height)
        UIGraphicsBeginImageContextWithOptions(size, false, 0)
        let ctx = UIGraphicsGetCurrentContext()
        ctx!.setAlpha(0.15)
        color.setFill()
        UIRectFill(rect)
        guard let image: UIImage = UIGraphicsGetImageFromCurrentImageContext() else {
            return nil
        }
        UIGraphicsEndImageContext()
        return image
    }
}

extension UISearchBar {
    @IBInspectable var shadowOffset : CGSize {
        get {
            return layer.shadowOffset
        }
        set {
            layer.shadowOffset = newValue
        }
    }
    @IBInspectable var shadowOpacity : Float {
        get {
            return layer.shadowOpacity
        }
        set {
            layer.shadowOpacity = newValue
        }
    }
}

extension UIImageView {
    @IBInspectable var shadowOffset : CGSize {
        get {
            return layer.shadowOffset
        }
        set {
            layer.shadowOffset = newValue
        }
    }
    @IBInspectable var shadowOpacity : Float {
        get {
            return layer.shadowOpacity
        }
        set {
            layer.shadowOpacity = newValue
        }
    }
}
