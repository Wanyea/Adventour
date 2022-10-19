//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit
import FirebaseAuth




extension UIView{
    @IBInspectable var shadowColor:
    UIColor? {
        get{
            if let color = layer.shadowColor{
                return UIColor(cgColor: color)
            }
            else{
                return nil
            }
        }
        set {layer.shadowColor = newValue?.cgColor}
        

}
    func addShadow(shadowColor: CGColor = UIColor.black.cgColor, shadowOffset: CGSize = CGSize(width: 1.0, height: 2.0), shadowOpacity: Float = 0.4, shadowRadius: CGFloat = 3.0)
    {
        layer.shadowColor = shadowColor
        layer.shadowOffset = shadowOffset
        layer.shadowOpacity = shadowOpacity
        layer.masksToBounds = false
    }
}



class forgotPassword: UIViewController
{
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var text: UIView!
    

    @IBOutlet weak var emailLabelText: UILabel!
    @IBOutlet weak var forgotPassEmail: UITextField!
    @IBAction func back(_ sender: Any) {
        self.dismiss(animated: true, completion: nil)
    }
    
    @IBAction func sendPass(_ sender: UIButton)
    {
        print(forgotPassEmail.text!)
        let emailPattern = #"^\S+@\S+\.\S+$"#
        
        var result = forgotPassEmail.text!.range(
            of: emailPattern,
            options: .regularExpression
        )
        
        let validEmail = (result != nil)
        print(validEmail)
        if(validEmail)
        {
            Auth.auth().sendPasswordReset(withEmail: forgotPassEmail.text!)
            {
                error in
                if error != nil
                {
                    self.emailLabelText.text = "If there is an account associated with this email the password reset has been sent. Please check your spam folder as the reset email sometimes gets sent to spam"
                    self.emailLabelText.isHidden = false
                }
                
                else
                {
                    self.emailLabelText.text = "If there is an account associated with this email the password reset has been sent. Please check your spam folder as the reset email sometimes gets sent to spam"
                    self.emailLabelText.isHidden = false
                }
                
            }
        }
        
        else{
            self.emailLabelText.text = "Please enter a valid email."
            self.emailLabelText.isHidden = false
        }
        
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        text.layer.shadowOpacity = 1.0
        text.layer.shadowOffset = CGSize(width: 1.0,height: 1.0)
        text.layer.shadowColor = UIColor.black.cgColor
    }
}

class LoginViewController: UIViewController {
    
   
    
    
    @IBAction func unwindHome(_ segue: UIStoryboardSegue){
        
    }
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Set icons into text field
        email.leftViewMode = UITextField.ViewMode.always
        email.leftView = emailIcon
        
        password.leftViewMode = UITextField.ViewMode.always
        password.leftView = passwordIcon
        // Do any additional setup after loading the view.
        
        errorMessage.isHidden = true
    }
    
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var password: UITextField!
    
    @IBOutlet weak var emailIcon: UIImageView!
    @IBOutlet weak var passwordIcon: UIImageView!
    
    @IBOutlet weak var emailError: UILabel!
    @IBOutlet weak var errorAlert: UILabel!
    @IBOutlet weak var passwordError: UILabel!
    @IBOutlet weak var errorMessage: UILabel!
    
    var emailFlag: Bool!
    var passwordFlag: Bool!
    


    @IBAction func loginTapped(_ sender: Any) {

        Auth.auth().signIn(withEmail: email.text!, password: password.text!) { result, error in
            if error != nil {
                self.errorMessage.text = "Invalid username and password combination."
                self.errorMessage.isHidden = false
            } else {
                self.errorMessage.isHidden = true
                UserDefaults.standard.set(true, forKey: "isUserLoggedIn")
                self.switchToTabController()
            }
        }

        
    }
    
    
    func switchToTabController() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let mainTabBarController = storyboard.instantiateViewController(identifier: "TabBarViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
    }
    
    
    //override func viewWillAppear(_ animated: Bool) {
        //super.viewWillAppear(animated)
        //navigationController?.setNavigationBarHidden(true, animated: animated)
    //}
}


