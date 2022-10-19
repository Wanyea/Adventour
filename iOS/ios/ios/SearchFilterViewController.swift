//
//  SearchFilterViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/18/22.
//

import UIKit

class SearchFilterViewController: UIViewController {

    @IBOutlet weak var exitIndicator: UIView!
    @IBOutlet weak var socialSwitch: UISwitch!
    @IBOutlet weak var outdoorsySwitch: UISwitch!
    @IBOutlet weak var cultureSwitch: UISwitch!
    @IBOutlet weak var hungrySwitch: UISwitch!
    @IBOutlet weak var romanticSwitch: UISwitch!
    @IBOutlet weak var geekySwitch: UISwitch!
    @IBOutlet weak var spirtualSwitch: UISwitch!
    @IBOutlet weak var sportySwitch: UISwitch!
    @IBOutlet weak var chillSwitch: UISwitch!
    @IBOutlet weak var shoppySwitch: UISwitch!
    @IBOutlet weak var pamperedSwitch: UISwitch!
    @IBOutlet weak var distanceSlider: UISlider!
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.exitIndicator?.layer.cornerRadius = 3
        setFilters()
        distanceVar = Int(distanceSlider.value)
        setDistanceLabel(distanceVar: distanceVar)
        // Do any additional setup after loading the view.
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.performSegue(withIdentifier: "getOutlets", sender: Any?.self)
        
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
        self.hungrySwitch?.isOn = UserDefaults.standard.bool(forKey: "hungrySwitch")
        self.romanticSwitch?.isOn = UserDefaults.standard.bool(forKey: "romanticSwitch")
        self.geekySwitch?.isOn = UserDefaults.standard.bool(forKey: "geekySwitch")
        self.spirtualSwitch?.isOn = UserDefaults.standard.bool(forKey: "spirtualSwitch")
        self.sportySwitch?.isOn = UserDefaults.standard.bool(forKey: "sportySwitch")
        self.chillSwitch?.isOn = UserDefaults.standard.bool(forKey: "chillSwitch")
        self.shoppySwitch?.isOn = UserDefaults.standard.bool(forKey: "shoppySwitch")
        self.pamperedSwitch?.isOn = UserDefaults.standard.bool(forKey: "pamperedSwitch")
        self.distanceSlider?.value = UserDefaults.standard.value(forKey: "distanceSlider") as! Float
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */

}
