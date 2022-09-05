//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit


class LoginViewController: UIViewController {
    
   
    
    
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        username.leftViewMode = UITextField.ViewMode.always
        username.leftView = displayNameIcon
        
        
        password.leftViewMode = UITextField.ViewMode.always
        password.leftView = passwordIcon
        // Do any additional setup after loading the view.
    }
    
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var displayNameIcon: UIImageView!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordIcon: UIImageView!
    @IBOutlet weak var usernameError: UILabel!
    @IBOutlet weak var errorAlert: UILabel!
    @IBOutlet weak var passwordError: UILabel!
    var userFlag: Bool!
    var passwordFlag: Bool!
    
    
    @IBAction func Button(sender: AnyObject) {
        if let text = username.text, text.isEmpty {
            usernameError.isHidden = false
            userFlag = false
            
        }
        else{
            usernameError.isHidden = true
            userFlag = true
        }
        if let text = password.text, text.isEmpty {
            passwordError.isHidden = false
            passwordFlag = false
            
        }
        else{
            passwordError.isHidden = true
            passwordFlag = true
        }
        
        if (userFlag == true) && (passwordFlag == true) {
            errorAlert.isHidden = true
        }
        
        else{
            errorAlert.isHidden = false
        }
    }

    @IBAction func loginTapped(_ sender: Any) {
        
                // ...
                // after login is done, maybe put this in the login web service completion block

                let storyboard = UIStoryboard(name: "Main", bundle: nil)
               
                let mainTabBarController = storyboard.instantiateViewController(identifier: "MainTabBarController")
                
                // This is to get the SceneDelegate object from your view controller
                // then call the change root view controller function to change to main tab bar
                (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
            }
    
    //override func viewWillAppear(_ animated: Bool) {
        //super.viewWillAppear(animated)
        //navigationController?.setNavigationBarHidden(true, animated: animated)
    //}
}


