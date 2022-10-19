import UIKit
import MapKit
import FirebaseAuth
import GooglePlaces
var temp = ""
var lon = Double(0)
var lat = Double(0)

class StartViewController: UIViewController {
    
    var user: User!
    
    // Filter Outlets
    var exitIndicator: UIView!
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
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var websiteLabel: UILabel!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    
    // Button Outlets
    @IBOutlet weak var notNow: UIButton!
    
    var fsq_id: String!
    var ids: [String]? = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
               
        self.websiteLabel?.adjustsFontSizeToFitWidth = true
        self.websiteLabel?.minimumScaleFactor = 0.75
        self.websiteLabel?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.websiteTapped)))
        self.phoneLabel?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.phoneTapped)))

        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            self.switchToLoggedOut()
        }
        
        
        
        //Adding the bordercolor and corner radius on the not now button. Actual border is in its runtime attributes.
        notNow?.layer.borderColor = UIColor.red.cgColor
        notNow?.layer.cornerRadius = 15
        // Do any additional setup after loading the view.
        placesClient = GMSPlacesClient.shared()
        searchBar?.text = temp
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
        searchBar?.text = temp
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        self.websiteClickable?.adjustsFontSizeToFitWidth = true
        self.websiteClickable?.minimumScaleFactor = 0.75
        self.websiteClickable?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.websiteTapped)))
        self.phoneClickable?.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.phoneTapped)))
        
        if let ids = self.ids {
            print("Start, These is the ids: ", ids)
        } else {
            print("Start, These ids is nil")
        }
        self.hideCardInfo()
        
        
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let destinationVC = segue.destination as? MapViewController {
            preparePlace()
            destinationVC.ids = self.ids
        }
        
        if let destinationVC = segue.destination as? SearchFilterViewController {
            if let social = self.socialSwitch {
                destinationVC.socialSwitch = social
                UserDefaults.standard.set(social.isOn, forKey: "socialSwitch")
            }
            if let outdoorsy = self.outdoorsySwitch {
                destinationVC.outdoorsySwitch = outdoorsy
                UserDefaults.standard.set(outdoorsy.isOn, forKey: "outdoorsySwitch")
            }
            if let culture = self.cultureSwitch {
                destinationVC.cultureSwitch = culture
                UserDefaults.standard.set(culture.isOn, forKey: "cultureSwitch")
            }
            if let hungry = self.hungrySwitch {
                destinationVC.hungrySwitch = hungry
                UserDefaults.standard.set(hungry.isOn, forKey: "hungrySwitch")
            }
            if let romantic = self.romanticSwitch {
                destinationVC.romanticSwitch = romantic
                UserDefaults.standard.set(romantic.isOn, forKey: "romanticSwitch")
            }
            if let geeky = self.geekySwitch {
                destinationVC.geekySwitch = geeky
                UserDefaults.standard.set(geeky.isOn, forKey: "geekySwitch")
            }
            if let spiritual = self.spirtualSwitch {
                destinationVC.spirtualSwitch = spiritual
                UserDefaults.standard.set(spiritual.isOn, forKey: "spirtualSwitch")
            }
            if let sporty = self.sportySwitch {
                destinationVC.sportySwitch = sporty
                UserDefaults.standard.set(sporty.isOn, forKey: "sportySwitch")
            }
            if let chill = self.chillSwitch {
                destinationVC.chillSwitch = chill
                UserDefaults.standard.set(chill.isOn, forKey: "chillSwitch")
            }
            if let shoppy = self.shoppySwitch {
                destinationVC.shoppySwitch = shoppy
                UserDefaults.standard.set(shoppy.isOn, forKey: "shoppySwitch")
            }
            if let pampered = self.pamperedSwitch {
                destinationVC.pamperedSwitch = pampered
                UserDefaults.standard.set(pampered.isOn, forKey: "pamperedSwitch")
            }
            if let distance = self.distanceSlider {
                destinationVC.distanceSlider = distance
                UserDefaults.standard.set(distance.value, forKey: "distanceSlider")
            }
        }
        
    }
    
    
    @IBAction func getAdventourPlace(_ sender: Any) {
        self.hideCardInfo()
        self.activityIndicator?.isHidden = false
        self.activityIndicator?.startAnimating()
        getAdventourLocation()
    }
    
    
    @IBAction func getOutlets(sender: UIStoryboardSegue) {
        if let source = sender.source as? SearchFilterViewController {
            print("WE MADE IT")
            self.socialSwitch = source.socialSwitch
            self.outdoorsySwitch = source.outdoorsySwitch
            self.cultureSwitch = source.cultureSwitch
            self.hungrySwitch = source.hungrySwitch
            self.romanticSwitch = source.romanticSwitch
            self.geekySwitch = source.geekySwitch
            self.spirtualSwitch = source.spirtualSwitch
            self.sportySwitch = source.sportySwitch
            self.chillSwitch = source.chillSwitch
            self.shoppySwitch = source.shoppySwitch
            self.pamperedSwitch = source.pamperedSwitch
            self.distanceSlider = source.distanceSlider
        }
    }
    
    
    //makes the sliders "step".
    
    
    @IBAction func unwindHome(_ segue: UIStoryboardSegue){
        
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
        
        
        
        let params: [String: Any] = [
            "uid": self.user.uid,
            "ll": "28.592474256389895,-81.3500389284532",
            "radius": self.milesToMeters(distanceInMiles: self.distanceSlider?.value ?? 0),
            "categories": getCategoryString()
        ]
        
        let url = URL(string: "https://adventour-183a0.uc.r.appspot.com/get-adventour-place")
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
                    if let data = dataDict["data"] as? [String: Any] {
                        
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
                            self.activityIndicator?.stopAnimating()
                            self.showCardInfo()
                        }
                        
                    }
                    
                }
            }
        }
        task.resume()
    }
    
    func getCategoryString() -> String {
        var categories: String = ""
        if self.socialSwitch.isOn {
            let socialString = "10001,10003,10006,10007,10009,10017,10019,10022,10023,10039,10048,10055,10056,12004,14003,14009,10015,"
            categories.append(socialString)
        }
        if self.outdoorsySwitch.isOn {
            let outdoorsyString = "10014,10044,10055,10056,16002,16003,16004,16005,16006,16008,16009,16011,16012,16013,16016,16017,16018,16019,16021,16022,16023,16024,16027,16028,16032,16043,16044,16046,16048,16049,16051,16052,19002,19003,19008,19021,"
            categories.append(outdoorsyString)
        }
        if self.cultureSwitch.isOn {
            let culturedString = "10002,10004,10016,10024,10028,10031,10030,10042,17002,17003,10043,17018,10047,10056,11005,17098,17113,11140,12005,12065,12066,12080,12081,16011,16024,16007,16020,16025,16026,16031,17103,16047,"
            categories.append(culturedString)
        }
        if self.hungrySwitch.isOn {
            let hungryString = "13028,13053,13054,13065,13001,13002,13032,13040,13059,13381,13382,"
            categories.append(hungryString)
        }
        if self.romanticSwitch.isOn {
            let romanticString = "10004,10016,10024,10023,11140,16005,11073,"
            categories.append(romanticString)
        }
        if self.geekySwitch.isOn {
            let geekyString = "10003,10015,10018,17018,17022,17091,17027,17135,10044,10054,12080,12081,"
            categories.append(geekyString)
        }
        if self.spirtualSwitch.isOn {
            let spirtualString = "12098,"
            categories.append(spirtualString)
        }
        if self.sportySwitch.isOn {
            let sportyString = "10006,10014,10019,10022,10023,10045,10048,18005,18008,18012,18019,18020,18021,18029,18034,17117,18035,18036,18037,18039,18040,18048,18054,18057,18058,18064,18067,19002,"
            categories.append(sportyString)
        }
        if self.chillSwitch.isOn {
            let chillString = "10003,10006,10015,10020,10024,10025,10045,10056,11005,11073,12080,12081,19021,16003,16005,16032"
            categories.append(chillString)
        }
        if self.shoppySwitch.isOn {
            let shoppyString = "14009,17002,17002,17004,17018,17020,17022,17027,17024,17030,17031,17032,17039,17053,17054,17055,17056,17089,17091,17098,17107,17111,17113,17116,17117,17135,17138,17103,"
            categories.append(shoppyString)
        }
        if self.pamperedSwitch.isOn {
            let pamperedString = "11062,11063,11064,11071,11072,11073,11074,11070,17030,11140,15001,17020,"
            categories.append(pamperedString)
        }
        print(categories)
        return categories
    }

    func milesToMeters(distanceInMiles distance: Float) -> Int {
        return Int(distance * 1609.344)
    }
    
    func hideCardInfo() {
        self.locationPhoto?.isHidden = true
        self.nameLabel?.isHidden = true

        self.descriptionLabel?.isHidden = true
        self.websiteClickable?.isHidden = true
        self.phoneClickable?.isHidden = true

        
        self.phoneLabel?.isHidden = true
        self.websiteLabel?.isHidden = true
        self.activityIndicator?.isHidden = true
    }
    
    func showCardInfo() {
        self.locationPhoto?.isHidden = false
        self.nameLabel?.isHidden = false

        self.descriptionLabel?.isHidden = false
        self.websiteClickable?.isHidden = false
        self.phoneClickable?.isHidden = false


        
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

extension StartViewController: GMSAutocompleteViewControllerDelegate {

  // Handle the user's selection.
  func viewController(_ viewController: GMSAutocompleteViewController, didAutocompleteWith place: GMSPlace) {
    print("Place name: \(place.name)")
    print("Place ID: \(place.placeID)")
    print("Place attributions: \(place.attributions)")
    print("Place address: \(place.formattedAddress))")
    lat = place.coordinate.latitude
    lon = place.coordinate.longitude
    print("Place lat: \(lat)")
    print("Place lon: \(lon)")
    temp = place.formattedAddress!
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
