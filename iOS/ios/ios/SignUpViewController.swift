//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit


class SignUpViewController: UIViewController {
    
   
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        nickname.leftViewMode = UITextField.ViewMode.always
        nickname.leftView = nicknameIcon
        email.leftViewMode = UITextField.ViewMode.always
        email.leftView = emailIcon
        
        birthday.leftViewMode = UITextField.ViewMode.always
        birthday.leftView = birthdayIcon
        
        password.leftViewMode = UITextField.ViewMode.always
        password.leftView = passwordIcon
        
        passwordConfirm.leftViewMode = UITextField.ViewMode.always
        passwordConfirm.leftView = passwordConfirmIcon
        // Do any additional setup after loading the view.
    }
    
    @IBOutlet weak var nicknameIcon: UIImageView!
    @IBOutlet weak var emailIcon: UIImageView!
    @IBOutlet weak var birthdayIcon: UIImageView!
    @IBOutlet weak var passwordIcon: UIImageView!
    @IBOutlet weak var passwordConfirmIcon: UIImageView!
    @IBOutlet weak var nickname: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var birthday: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordConfirm: UITextField!
    @IBOutlet weak var nicknameError: UILabel!
    @IBOutlet weak var emailError: UILabel!
    @IBOutlet weak var birthdayError: UILabel!
    @IBOutlet weak var errorAlert: UILabel!
    @IBOutlet weak var passwordError: UILabel!
    @IBOutlet weak var passwordConfirmError: UILabel!
    var nicknameFlag: Bool!
    var passwordFlag: Bool!
    var passwordConfirmFlag: Bool!
    var emailFlag: Bool!
    var birthdayFlag: Bool!
    
    @IBAction func Button(sender: AnyObject) {
        if let text = nickname.text, text.isEmpty {
            nicknameError.isHidden = false
            nicknameFlag = false
            
        }
        else{
            nicknameError.isHidden = true
            nicknameFlag = true
        }
        if let text = password.text, text.isEmpty {
            passwordError.isHidden = false
            passwordFlag = false
            
        }
        else{
            passwordError.isHidden = true
            passwordFlag = true
        }
        
        if let text = passwordConfirm.text, text.isEmpty {
            passwordConfirmError.isHidden = false
            passwordConfirmFlag = false
            
        }
        else{
            passwordConfirmError.isHidden = true
            passwordConfirmFlag = true
        }
        if let text = email.text, text.isEmpty {
            emailError.isHidden = false
            emailFlag = false
            
        }
        else{
            emailError.isHidden = true
            emailFlag = true
        }
        if let text = birthday.text, text.isEmpty {
            birthdayError.isHidden = false
            birthdayFlag = false
            
        }
        else{
            birthdayError.isHidden = true
            birthdayFlag = true
        }
        if (nicknameFlag == true) && (passwordFlag == true) && (passwordConfirmFlag == true) && (emailFlag == true) && (birthdayFlag == true){
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
    //    super.viewWillAppear(animated)
    //    navigationController?.setNavigationBarHidden(true, animated: animated)
    //}
}


