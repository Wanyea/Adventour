import UIKit
import MapKit

class StartViewController: UIViewController {
    
    
    
    
    @IBOutlet weak var distanceLabel: UILabel!
    @IBOutlet weak var priceLabel: UILabel!
    @IBOutlet weak var notNow: UIButton!
    
    var ids: [String]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Adding the bordercolor on the not now button. Actual border is in its runtime attributes.
        
        notNow?.layer.borderColor = UIColor.red.cgColor
        // Do any additional setup after loading the view.
    }
    
    
    override func viewWillAppear(_ animated: Bool) {
        if let ids = self.ids {
            print("These is the ids: ", ids)
        } else {
            print("The ids is nil")
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
        


}
