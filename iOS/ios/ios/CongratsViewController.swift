import UIKit
import MapKit


extension UIButton{
    
        @IBInspectable var borderWidth : CGFloat{
        get{
            return layer.borderWidth
        }
        set{
            layer.borderWidth = newValue
        }
        }
        @IBInspectable var borderColor : UIColor?{
            get{
                let color = UIColor.init(cgColor: layer.borderColor!)
                return color
            }
            set{
                layer.borderColor = newValue?.cgColor
            }
        }
    }
class CongratsViewController: UIViewController {
    
    @IBOutlet weak var viewAdventour: UIButton!
    @IBOutlet weak var home: UIButton!
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        //Adding the bordercolor on the not now button. Actual border is in its runtime attributes.
       
        
        // Do any additional setup after loading the view.
    }
}
    
