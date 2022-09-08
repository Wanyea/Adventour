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
        signUpButton.isEnabled = false
        password.addTarget(self, action: #selector(comparePassword(_:)), for: .editingChanged)
        passwordConfirm.addTarget(self, action: #selector(comparePassword(_:)), for: .editingChanged)
        email.addTarget(self, action: #selector(verifyEmail(_:)), for: .editingChanged)
        nickname.addTarget(self, action: #selector(verifyNickname(_:)), for: .editingChanged)

        
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
    
    @IBOutlet weak var birthdate: UIDatePicker!
    
    var nicknameFlag: Bool!
    var passwordFlag: Bool!
    var passwordConfirmFlag: Bool!
    var emailFlag: Bool!
    var birthdayFlag: Bool!
    
    @IBOutlet weak var signUpButton: UIButton!
    
    //    @IBAction func Button(sender: AnyObject) {
//        if let text = nickname.text, text.isEmpty {
//            nicknameError.isHidden = false
//            nicknameFlag = false
//
//        }
//        else{
//            nicknameError.isHidden = true
//            nicknameFlag = true
//        }
//        if let text = password.text, text.isEmpty {
//            passwordError.isHidden = false
//            passwordFlag = false
//
//        }
//        else{
//            passwordError.isHidden = true
//            passwordFlag = true
//        }
//
//        if let text = passwordConfirm.text, text.isEmpty {
//            passwordConfirmError.isHidden = false
//            passwordConfirmFlag = false
//
//        }
//        else{
//            passwordConfirmError.isHidden = true
//            passwordConfirmFlag = true
//        }
//        if let text = email.text, text.isEmpty {
//            emailError.isHidden = false
//            emailFlag = false
//
//        }
//        else{
//            emailError.isHidden = true
//            emailFlag = true
//        }
//        if let text = birthday.text, text.isEmpty {
//            birthdayError.isHidden = false
//            birthdayFlag = false
//
//        }
//        else{
//            birthdayError.isHidden = true
//            birthdayFlag = true
//        }
//        if (nicknameFlag == true) && (passwordFlag == true) && (passwordConfirmFlag == true) && (emailFlag == true) && (birthdayFlag == true){
//            errorAlert.isHidden = true
//        }
//
//        else{
//            errorAlert.isHidden = false
//        }
//    }

    @IBAction func signUpTapped(_ sender: Any) {

        
        
        
//        print(comparePassword(password: password, confirmPassword: passwordConfirm))
        

//        let storyboard = UIStoryboard(name: "Main", bundle: nil)
//       
//        let mainTabBarController = storyboard.instantiateViewController(identifier: "MainTabBarController")
//        
//        // This is to get the SceneDelegate object from your view controller
//        // then call the change root view controller function to change to main tab bar
//        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
    }
    
    @objc func verifyNickname(_ textField: UITextField) {
        if nickname.text!.count > 25 {
            nicknameError.isHidden = false
            errorAlert.isHidden = false
            signUpButton.isEnabled = false
        } else {
            nicknameError.isHidden = true
            errorAlert.isHidden = true
            
        }
    }
    
    @objc func verifyEmail(_ textField: UITextField) {
//        verify that email has @ and period
        let emailPattern = #"^\S+@\S+\.\S+$"#
        
        var result = email.text!.range(
            of: emailPattern,
            options: .regularExpression
        )
        
        let validEmail = (result != nil)
        
        if (!validEmail) {
            emailError.isHidden = false
            errorAlert.isHidden = false
            signUpButton.isEnabled = false
        } else {
            emailError.isHidden = true
            errorAlert.isHidden = true
            
        }
        
    }
    
    @objc func comparePassword(_ textField: UITextField) {
//        verify password and confirmPassword are the same
        if (password.text == passwordConfirm.text) {
            passwordConfirmError.isHidden = true
            errorAlert.isHidden = true
            
        }
        else {
            passwordConfirmError.isHidden = false
            errorAlert.isHidden = false
            signUpButton.isEnabled = false
        }
        
        
        
    }
    
    func verifyBirthdate(bithdate: UIDatePicker) -> Bool {
//        verify user is 13 or older
        
        return true;
    }

    
    //override func viewWillAppear(_ animated: Bool) {
    //    super.viewWillAppear(animated)
    //    navigationController?.setNavigationBarHidden(true, animated: animated)
    //}
}


