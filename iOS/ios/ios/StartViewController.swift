import UIKit
import MapKit
import FirebaseAuth

class StartViewController: UIViewController {
    
    var user: User!
    
    @IBOutlet weak var locationPhoto: UIImageView!
    @IBOutlet weak var nameLabel: UILabel!

    @IBOutlet weak var descriptionLabel: UILabel!
    @IBOutlet weak var websiteLabel: UILabel!
    @IBOutlet weak var phoneLabel: UILabel!
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var notNow: UIButton!
    
    var fsq_id: String!
    var ids: [String]? = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.websiteLabel.adjustsFontSizeToFitWidth = true
        self.websiteLabel.minimumScaleFactor = 0.75
        self.websiteLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.websiteTapped)))
        self.phoneLabel.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(StartViewController.phoneTapped)))
        if let user = Auth.auth().currentUser {
            self.user = user
        } else {
            self.switchToLoggedOut()
        }
        
        
        
        //Adding the bordercolor and corner radius on the not now button. Actual border is in its runtime attributes.
        notNow?.layer.borderColor = UIColor.red.cgColor
        notNow?.layer.cornerRadius = 15
        // Do any additional setup after loading the view.
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        if let ids = self.ids {
            print("Start, These is the ids: ", ids)
        } else {
            print("Start, These ids is nil")
        }
        
        getAdventourLocation()
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        preparePlace()
        if let destinationVC = segue.destination as? MapViewController {
            destinationVC.ids = self.ids
        }
        
    }
    
    
    @IBAction func unwindHome(sender: UIStoryboardSegue) {
        if let sourceViewController = sender.source as? MapViewController {
            self.ids = sourceViewController.ids
        }
    }
    
    
    //makes the sliders "step".
    var priceVar: String!
    var distanceVar: Int!
    let step: Float = 1
    @IBAction func priceSliderValueChanged(sender: UISlider) {
        let priceRoundedValue = round(sender.value / step) * step
        sender.value = priceRoundedValue
        if priceRoundedValue == 1.0{
            priceVar = "$"
        }
        if priceRoundedValue == 2.0{
            priceVar = "$$"
        }
        if priceRoundedValue == 3.0{
            priceVar = "$$$"
        }
        if priceRoundedValue == 4.0{
            priceVar = "$$$$"
        }
        
    
        priceLabel.text = "Price: \(priceVar ?? "$")"
    }
    
    @IBAction func unwindHome(_ segue: UIStoryboardSegue){
        
    }
    
    @IBAction func distanceSliderValueChanged(sender: UISlider) {
        let distanceRoundedValue = round(sender.value / step) * step
        sender.value = distanceRoundedValue
        distanceVar = Int(distanceRoundedValue)
        if distanceVar == 1
        {
            distanceLabel.text = "Distance: \(distanceVar ?? 1) mile"
        }
        
        else{
            distanceLabel.text = "Distance: \(distanceVar ?? 1) miles"
        }
        
    }
    
    func preparePlace() {
        self.ids?.insert(self.fsq_id, at: 0)
    }
    
    @objc func websiteTapped() {
        if let string = websiteLabel.text {
            if let url = URL(string: string) {
                UIApplication.shared.open(url, options: [:], completionHandler: nil)
            }
        }
    }
    
    @objc func phoneTapped() {
        if let string = phoneLabel.text {
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
            "radius": "10000",
            "categories": "10014,10044,10055,10056,16002,16003,16004,16005,16006,16008,16009,16011,16012,16013,16016,16017,16019,16018,16021,16022,16023,16024,16027,16028,16032,16043,16044,16046,16048,16049,16051,16052,19002,19003,19008,19021"
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
                            }
                            
                            if let name = data["name"] as? String {
                                self.nameLabel.text = name
                            } else {
                                self.nameLabel.text = "This place does not have a name."
                            }
                            
                            if let description = data["description"] as? String {
                                self.descriptionLabel.text = description
                            } else {
                                self.descriptionLabel.text = "This place does not have a listed description."
                            }
                            
                            if let tel = data["tel"] as? String {
                                self.phoneLabel.text = tel
                            } else {
                                self.phoneLabel.text = "N/A"
                            }
                            
                            if let website = data["website"] as? String {
                                self.websiteLabel.text = website
                            } else {
                                self.websiteLabel.text = "N/A"
                            }
                        }
                        
                    }
                    
                }
            }
        }
        task.resume()
    }

    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoggedOutViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc)
    }
    
}
