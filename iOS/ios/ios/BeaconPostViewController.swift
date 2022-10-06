import UIKit
import MapKit

extension UIImageView{
    @IBInspectable var borderColor : UIColor? {
        get{
            if let color = layer.borderColor{
                return UIColor(cgColor: color)
            }
            else{
                return nil
            }
        }
        set {layer.borderColor = newValue?.cgColor}
}
}


class BeaconPostViewController: UIViewController {
    override func viewDidLoad() {
        super.viewDidLoad()
        
        
        
    }
}
    

