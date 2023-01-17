//
//  SearchFilterViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/18/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class SearchFilterViewController: UIViewController {

    @IBOutlet weak var exitIndicator: UIView!
    @IBOutlet weak var socialSwitch: UISwitch!
    @IBOutlet weak var outdoorsySwitch: UISwitch!
    @IBOutlet weak var cultureSwitch: UISwitch!
    @IBOutlet weak var snackishSwitch: UISwitch!
    @IBOutlet weak var starvingSwitch: UISwitch!
    @IBOutlet weak var romanticSwitch: UISwitch!
    @IBOutlet weak var geekySwitch: UISwitch!
    @IBOutlet weak var spirtualSwitch: UISwitch!
    @IBOutlet weak var sportySwitch: UISwitch!
    @IBOutlet weak var chillSwitch: UISwitch!
    @IBOutlet weak var shoppySwitch: UISwitch!
    @IBOutlet weak var pamperedSwitch: UISwitch!
    @IBOutlet weak var twentyOneSwitch: UISwitch!
    @IBOutlet weak var distanceSlider: UISlider!
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    
    @IBOutlet weak var twentyOneView: UIView!
    var user: User!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        updateFilters()
        self.performSegue(withIdentifier: "getOutlets", sender: Any?.self)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        checkTwentyOne()
        setFilters()
        self.exitIndicator?.layer.cornerRadius = 3
        distanceVar = Int(distanceSlider.value)
        setDistanceLabel(distanceVar: distanceVar)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        updateFilters()
    }

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
    
    @IBAction func distanceSliderValueChanged(sender: UISlider) {
        let distanceRoundedValue = round(sender.value / step) * step
        sender.value = distanceRoundedValue
        distanceVar = Int(distanceRoundedValue)
        setDistanceLabel(distanceVar: distanceVar)
        
    }
    
    func setDistanceLabel(distanceVar: Int) {
        if distanceVar == 1
        {
            distanceLabel.text = "Distance: \(distanceVar) mile"
        }
        
        else{
            distanceLabel.text = "Distance: \(distanceVar) miles"
        }
    }
    
    func setFilters() {
        self.socialSwitch?.isOn = UserDefaults.standard.bool(forKey: "socialSwitch")
        self.outdoorsySwitch?.isOn = UserDefaults.standard.bool(forKey: "outdoorsySwitch")
        self.cultureSwitch?.isOn = UserDefaults.standard.bool(forKey: "cultureSwitch")
        self.snackishSwitch?.isOn = UserDefaults.standard.bool(forKey: "snackishSwitch")
        self.starvingSwitch?.isOn = UserDefaults.standard.bool(forKey: "starvingSwitch")
        self.romanticSwitch?.isOn = UserDefaults.standard.bool(forKey: "romanticSwitch")
        self.geekySwitch?.isOn = UserDefaults.standard.bool(forKey: "geekySwitch")
        self.spirtualSwitch?.isOn = UserDefaults.standard.bool(forKey: "spirtualSwitch")
        self.sportySwitch?.isOn = UserDefaults.standard.bool(forKey: "sportySwitch")
        self.chillSwitch?.isOn = UserDefaults.standard.bool(forKey: "chillSwitch")
        self.shoppySwitch?.isOn = UserDefaults.standard.bool(forKey: "shoppySwitch")
        self.pamperedSwitch?.isOn = UserDefaults.standard.bool(forKey: "pamperedSwitch")
        self.twentyOneSwitch?.isOn = UserDefaults.standard.bool(forKey: "twentyOneSwitch")
        if let value = UserDefaults.standard.value(forKey: "distanceSlider") as? Float {
            print("set distance: ", value)
            self.distanceSlider?.value = value
        }
        
    }
    
    func updateFilters() {
        UserDefaults.standard.set(self.socialSwitch?.isOn, forKey: "socialSwitch")
        UserDefaults.standard.set(self.outdoorsySwitch?.isOn, forKey: "outdoorsySwitch")
        UserDefaults.standard.set(self.cultureSwitch?.isOn, forKey: "cultureSwitch")
        UserDefaults.standard.set(self.snackishSwitch?.isOn, forKey: "snackishSwitch")
        UserDefaults.standard.set(self.starvingSwitch?.isOn, forKey: "starvingSwitch")
        UserDefaults.standard.set(self.romanticSwitch?.isOn, forKey: "romanticSwitch")
        UserDefaults.standard.set(self.geekySwitch?.isOn, forKey: "geekySwitch")
        UserDefaults.standard.set(self.spirtualSwitch?.isOn, forKey: "spirtualSwitch")
        UserDefaults.standard.set(self.sportySwitch?.isOn, forKey: "sportySwitch")
        UserDefaults.standard.set(self.chillSwitch?.isOn, forKey: "chillSwitch")
        UserDefaults.standard.set(self.shoppySwitch?.isOn, forKey: "shoppySwitch")
        UserDefaults.standard.set(self.pamperedSwitch?.isOn, forKey: "pamperedSwitch")
        UserDefaults.standard.set(self.twentyOneSwitch?.isOn, forKey: "twentyOneSwitch")
        print("update distance: ", self.distanceSlider?.value)
        UserDefaults.standard.set(self.distanceSlider?.value, forKey: "distanceSlider")
        print("updated distance: ", UserDefaults.standard.value(forKey: "distanceSlider"))
    }
    
    func checkTwentyOne() {
        if self.user != nil {
            let db = Firestore.firestore()
            db.collection("Adventourists")
                .document(self.user.uid)
                .getDocument { snap, error in
                    if let error = error {
                        print(error)
                        return
                    } else {
                        if let data = snap?.data() {
                            if let birthdate = data["birthdate"] as? Timestamp {
                                print(birthdate.dateValue())
                                if self.isTwentyOne(birthdate: birthdate.dateValue()) {
                                    self.twentyOneView.isHidden = false
                                } else {
                                    self.twentyOneView.isHidden = true
                                }
                            }
                        }
                    }
                }
        }
    }
    
    func isTwentyOne(birthdate: Date) -> Bool {
        
        if birthdate.age >= 21 {
            return true
        } else {
            return false
        }
    }
}

extension Date {
    var age: Int { Calendar.current.dateComponents([.year], from: self, to: Date()).year! }
}
