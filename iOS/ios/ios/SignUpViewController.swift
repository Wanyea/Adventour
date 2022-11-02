//
//  LoginViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/24/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore


class SignUpViewController: UIViewController {
    
    @IBOutlet weak var nicknameIcon: UIImageView!
    @IBOutlet weak var emailIcon: UIImageView!
    @IBOutlet weak var birthdayIcon: UIImageView!
    @IBOutlet weak var passwordIcon: UIImageView!
    @IBOutlet weak var passwordConfirmIcon: UIImageView!
    @IBOutlet weak var firstNameIcon: UIImageView!
    @IBOutlet weak var lastNameIcon: UIImageView!
    
    @IBOutlet weak var nickname: UITextField!
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var birthday: UITextField!
    @IBOutlet weak var password: UITextField!
    @IBOutlet weak var passwordConfirm: UITextField!
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    
    @IBOutlet weak var nicknameError: UILabel!
    @IBOutlet weak var emailError: UILabel!
    @IBOutlet weak var birthdayError: UILabel!
    @IBOutlet weak var errorAlert: UILabel!
    @IBOutlet weak var passwordError: UILabel!
    @IBOutlet weak var passwordConfirmError: UILabel!
    @IBOutlet weak var firstNameError: UILabel!
    @IBOutlet weak var lastNameError: UILabel!
    
    @IBOutlet weak var errorMessage: UILabel!
    
    @IBOutlet weak var birthdate: UIDatePicker!
    
    @IBOutlet weak var signUpButton: UIButton!
    @IBOutlet weak var nicknameNext: UIButton!
    
    
    var nicknameFlag: Bool!
    var passwordFlag: Bool!
    var passwordConfirmFlag: Bool!
    var emailFlag: Bool!
    var birthdayFlag: Bool!
    var birthdateString: String!
    
    
    var user: User!
    
    // Set up SignUpViewController
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if let user = Auth.auth().currentUser {
            self.user = user
            switchToTabController()
        } else {
            
        }
        
        // Set icons into textfield
        nickname?.leftViewMode = UITextField.ViewMode.always
        nickname?.leftView = nicknameIcon
        email?.leftViewMode = UITextField.ViewMode.always
        email?.leftView = emailIcon
        birthday?.leftViewMode = UITextField.ViewMode.always
        birthday?.leftView = birthdayIcon
        password?.leftViewMode = UITextField.ViewMode.always
        password?.leftView = passwordIcon
        passwordConfirm?.leftViewMode = UITextField.ViewMode.always
        passwordConfirm?.leftView = passwordConfirmIcon
        firstName?.leftViewMode = UITextField.ViewMode.always
        firstName?.leftView = firstNameIcon
        lastName?.leftViewMode = UITextField.ViewMode.always
        lastName?.leftView = lastNameIcon
        
        // Init button state
        self.signUpButton.isEnabled = false
        birthdayFlag = true
        passwordFlag = false
        self.errorMessage.text = ""
        
        // Targets for individual field errors
        password?.addTarget(self, action: #selector(comparePassword(_:)), for: .editingChanged)
        passwordConfirm?.addTarget(self, action: #selector(comparePassword(_:)), for: .editingChanged)
        email?.addTarget(self, action: #selector(verifyEmail(_:)), for: .editingChanged)
        nickname?.addTarget(self, action: #selector(verifyNickname(_:)), for: .editingChanged)
        birthdate?.addTarget(self, action: #selector(verifyBirthdate(_:)), for: .editingDidEnd)
        firstName?.addTarget(self, action: #selector(verifyFirstName(_:)), for: .editingChanged)
        lastName?.addTarget(self, action: #selector(verifyLastName(_:)), for: .editingChanged)
        
        // Targets for sign up button enable
        //password?.addTarget(self, action: #selector(verifySignUp(_:)), for: .editingChanged)
        //passwordConfirm?.addTarget(self, action: #selector(verifySignUp(_:)), for: .editingChanged)
        //email?.addTarget(self, action: #selector(verifySignUp(_:)), for: .editingChanged)
        //nickname?.addTarget(self, action: #selector(verifySignUp(_:)), for: .editingChanged)
        //birthdate?.addTarget(self, action: #selector(verifySignUp(_:)), for: .editingDidEnd)
        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        nickname?.endEditing(true)
        email?.endEditing(true)
        birthday?.endEditing(true)
        password?.endEditing(true)
        passwordConfirm?.endEditing(true)
    }
    
    @IBAction func signUpTapped(_ sender: Any) {
        
        Auth.auth().createUser(withEmail: email.text!, password: password.text!) { result, error in
            if error != nil {
                print("Error creating user: " + error.debugDescription)
                self.errorMessage.text = error?.localizedDescription
            } else {
                // Add as Adventourist in Firestore if successful
                Auth.auth().addStateDidChangeListener { auth, user in
                    if let user = user {
                        let db = Firestore.firestore()
                        
                        db.collection("Adventourists")
                            .document(user.uid)
                            .setData([
                                "nickname": self.nickname.text!,
                                "email": self.email.text!,
                                "isPrivate": true,
                                "birthday": self.birthdateString!,
                            ]) { err in
                                
                            }
                        
                    } else {
                        print("User is not logged in")
                    }
                }
                
            }
        }
        

        switchToTabController()
    }
    
    @objc func verifyNickname(_ textField: UITextField) {
        if nickname.text!.count > 16 {
            nicknameError.isHidden = false
            errorMessage.text = "Nickname must be 16 characters or less."
            //verifyAlert()
            signUpButton.isEnabled = false
            
            
        } else {
            nicknameError.isHidden = true
            
            errorMessage.text = ""
            if(firstNameError.isHidden && lastNameError.isHidden)
            {
                signUpButton.isEnabled = true
            }
            
            //verifyAlert()
            
        }
    }
    
    @objc func verifyFirstName(_ textField: UITextField) {
        
        let firstNamePattern = #"^[a-zA-Z]*$"#
        
        var result = firstName.text!.range(
            of: firstNamePattern,
            options: .regularExpression
        )
        
        let validFirstName = (result != nil)
        
        if (!validFirstName) {
            firstNameError.isHidden = false
            errorMessage.text = "First names must consist of only letters."
            //verifyAlert()
            signUpButton.isEnabled = false
        } else {
            firstNameError.isHidden = true
            errorMessage.text = ""
            if(nicknameError.isHidden && lastNameError.isHidden)
            {
                signUpButton.isEnabled = true
            }
            //verifyAlert()
            
        }
        
    }
    
    @objc func verifyLastName(_ textField: UITextField) {
        
        let lastNamePattern = #"^[a-zA-Z]*$"#
        
        var result = lastName.text!.range(
            of: lastNamePattern,
            options: .regularExpression
        )
        
        let validLastName = (result != nil)
        
        if (!validLastName) {
            lastNameError.isHidden = false
            errorMessage.text = "Last names must consist of only letters."
            //verifyAlert()
            signUpButton.isEnabled = false
        } else {
            lastNameError.isHidden = true
            errorMessage.text = ""
            if(firstNameError.isHidden && nicknameError.isHidden)
            {
                signUpButton.isEnabled = true
            }
            
        }
        
    }
    
    @objc func verifyEmail(_ textField: UITextField) {
//        verify that email has @ and period
//        if email.text == nil || email.text == "" {
//            emailError.isHidden = true
//            errorMessage.text = ""
//            verifyAlert()
//            return
//        }
        
        let emailPattern = #"^\S+@\S+\.\S+$"#
        
        var result = email.text!.range(
            of: emailPattern,
            options: .regularExpression
        )
        
        let validEmail = (result != nil)
        
        if (!validEmail) {
            emailError.isHidden = false
            errorMessage.text = "Please enter a valid email"
            //verifyAlert()
            signUpButton.isEnabled = false
        } else {
            emailError.isHidden = true
            errorMessage.text = ""
            signUpButton.isEnabled = true
            //verifyAlert()
            
        }
        
    }
    
    func isPasswordValid(_ password : String) -> Bool{
        let passwordTest = NSPredicate(format: "SELF MATCHES %@", "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$")
        
        return passwordTest.evaluate(with: password)
    }
    
    @objc func comparePassword(_ textField: UITextField) {
//        verify password and confirmPassword are the same
        
        if !isPasswordValid(password.text!) {
            print("password not valid")
            errorMessage.text = "Password must be 8 characters, contain one UPPER CASE letter, one LOWER CASE letter, one NUMBER, and one SPECIAL (!@#$&*) character"
            passwordError.isHidden = false
            passwordFlag = true
            signUpButton.isEnabled = false
            return
        } else {
            print("password valid!")
            errorMessage.text = ""
            passwordFlag = false
            passwordError.isHidden = true
            signUpButton.isEnabled = true
        }
        
        if (password.text == passwordConfirm.text) {
            passwordConfirmError.isHidden = true
            errorMessage.text = ""
            passwordFlag = false
            signUpButton.isEnabled = true
            //verifyAlert()
        }
        else {
            passwordConfirmError.isHidden = false
            errorMessage.text = "Passwords must match!"
            //verifyAlert()
            passwordFlag = true
            signUpButton.isEnabled = false
        }
        
        
        
        
    }
    
//    func verifyAlert() {
//        if (!nicknameError.isHidden ||
//            !emailError.isHidden ||
//            !birthdayError.isHidden ||
//            !passwordConfirmError.isHidden) {
//
//            errorAlert.isHidden = false
//        } else {
//            errorMessage.text = ""
//            errorAlert.isHidden = true
//        }
//    }
    
//    @objc func verifySignUp(_ button: UIButton) -> Bool {
//
//        if (errorAlert.isHidden &&
//            nickname.hasText &&
//            email.hasText &&
//            password.hasText &&
//            passwordConfirm.hasText &&
//            !birthdayFlag &&
//            !passwordFlag) {
//            errorMessage.text = ""
//            signUpButton.isEnabled = true
//            return true
//        } else {
//            signUpButton.isEnabled = false
//            return false
//        }
//    }
    
    @objc func verifyBirthdate(_ bithdate: UIDatePicker) {
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "MM/dd/yyyy"
        birthdateString = dateFormatter.string(from: birthdate.date)
        
        let components = birthdate.calendar.dateComponents([.day, .month, .year], from: birthdate.date)
        let birthDay = components.day
        let birthMonth = components.month
        let birthYear = components.year
        
        print("The date selected is: \(birthMonth!)/\(birthDay!)/\(birthYear!)")
        
        let currYear = Calendar.current.component(.year, from: Date())
        let currMonth = Calendar.current.component(.month, from: Date())
        let currDay = Calendar.current.component(.day, from: Date())
        
        if (currYear - birthYear! < 13) {
            birthdayError.isHidden = false
            errorMessage.text = "You must at least 13 years old to sign up!"
            birthdayFlag = true
        } else {
            if currYear - birthYear! == 13 {
                if currMonth < birthMonth! ||
                    (currMonth == birthMonth! && currDay < birthDay!) {
                    birthdayError.isHidden = false
                    errorMessage.text = "You must at least 13 years old to sign up!"
                    birthdayFlag = true
                    signUpButton.isEnabled = false
                } else {
                    birthdayError.isHidden = true
                    errorMessage.text = ""
                    signUpButton.isEnabled = true
                    //verifyAlert()
                    birthdayFlag = false
                }
            } else {
                birthdayError.isHidden = true
                errorMessage.text = ""
                signUpButton.isEnabled = true
                //verifyAlert()
                birthdayFlag = false
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

    
//    override func viewWillAppear(_ animated: Bool) {
//        super.viewWillAppear(animated)
//        
//        handle = Auth.auth().addStateDidChangeListener { auth, user in
//          // ...
//        }
//    }
//    
//    override func viewWillDisappear(_ animated: Bool) {
//        Auth.auth().removeStateDidChangeListener(handle!)
//    }
}


