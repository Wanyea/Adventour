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
        // Do any additional setup after loading the view.
    }
    
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var usernameError: UILabel!
    @IBOutlet weak var errorAlert: UILabel!
    @IBOutlet weak var passwordError: UILabel!
    var flag: Bool!
    var flag2: Bool!
    
    
    @IBAction func Button(sender: AnyObject) {
        if let text = username.text, text.isEmpty {
            usernameError.isHidden = false
            flag = false
            
        }
        else{
            usernameError.isHidden = true
            flag = true
        }
        if let text = password.text, text.isEmpty {
            passwordError.isHidden = false
            flag2 = false
            
        }
        else{
            passwordError.isHidden = true
            flag2 = true
        }
        
        if (flag == true) && (flag2 == true) {
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
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        navigationController?.setNavigationBarHidden(true, animated: animated)
    }
}


