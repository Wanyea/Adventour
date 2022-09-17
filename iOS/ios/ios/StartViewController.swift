import UIKit

class StartViewController: UIViewController {
    
    
    
    @IBOutlet weak var notNow: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //Adding the bordercolor on the not now button. Actual border is in its runtime attributes.
        
        notNow?.layer.borderColor = UIColor.red.cgColor
        // Do any additional setup after loading the view.
    }
    
    
    
    
    
    //makes the sliders "step".
    let step: Float = 1
    @IBAction func sliderValueChanged(sender: UISlider) {
        let roundedValue = round(sender.value / step) * step
        sender.value = roundedValue
    }
        


}
