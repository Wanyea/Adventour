import UIKit
import MapKit


extension UIButton {

    @IBInspectable var borderWidth : CGFloat {
        get {
            return layer.borderWidth
        }
        set {
            layer.borderWidth = newValue
        }
    }
    @IBInspectable var borderColor : UIColor?{
        get {
            let color = UIColor.init(cgColor: layer.borderColor!)
            return color
        }
        set {
            layer.borderColor = newValue?.cgColor
        }
    }
    @IBInspectable var cornerRadius : CGFloat {
        get {
            return layer.cornerRadius
        }
        set {
            layer.cornerRadius = newValue
        }
    }
    @IBInspectable var shadowOffset : CGSize {
        get {
            return layer.shadowOffset
        }
        set {
            layer.shadowOffset = newValue
        }
    }
    @IBInspectable var shadowRadius : CGFloat {
        get {
            return layer.shadowRadius
        }
        set {
            layer.shadowRadius = newValue
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

class CongratsViewController: UIViewController {
    
    @IBOutlet weak var viewAdventour: UIButton!
    @IBOutlet weak var home: UIButton!
    
    var locations: [String]!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        self.navigationController?.isNavigationBarHidden = true
        self.tabBarController?.tabBar.isHidden = true
        self.tabBarController?.tabBar.isTranslucent = true
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        print(segue.destination)
        if segue.destination is StartViewController {
            self.navigationController?.isNavigationBarHidden = false
            self.tabBarController?.tabBar.isHidden = false
            self.tabBarController?.tabBar.isTranslucent = false
        }
        if let dest = segue.destination as? AdventourSummaryNavigationController {
            print(self.locations)
            if let vc = dest.topViewController as? AdventourSummaryViewController {
                vc.locations = self.locations
            }
            dest.locations = self.locations
        }
        if let dest = segue.destination as? BeaconPostNavigationController {
            print(self.locations)
            if let vc = dest.topViewController as? BeaconPostViewController {
                vc.ids = self.locations
            }
            
        }
        
    }
    
    
}
    
