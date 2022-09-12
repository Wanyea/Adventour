//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit
import FirebaseAuth

class LoginViewController: UIViewController {
    
   
    
    
    
    
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


