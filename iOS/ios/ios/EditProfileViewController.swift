//
//  EditProfileViewController.swift
//  ios
//
//  Created by Wanyea on 10/13/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class EditProfileViewController: UIViewController{
    var androidPfpRef: Int!
    var iosPfpRef: String!

    var user: User!
    @IBOutlet weak var profilePic: UIImageView!
    
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var nickname: UITextField!
    @IBOutlet weak var firstName: UITextField!
    @IBOutlet weak var lastName: UITextField!
    @IBOutlet weak var mantra: UITextField!
    @IBOutlet weak var birthdate: UITextField!
    @IBOutlet weak var error: UILabel!
    
    @IBOutlet weak var saveButton: UIButton!
    
    
    var dataError: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //profilePic?.image = picture
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        getUserData()
        email?.addTarget(self, action: #selector(verifyUpdate(_:)), for: .editingChanged)
        nickname?.addTarget(self, action: #selector(verifyUpdate(_:)), for: .editingChanged)
        firstName?.addTarget(self, action: #selector(verifyUpdate(_:)), for: .editingChanged)
        lastName?.addTarget(self, action: #selector(verifyUpdate(_:)), for: .editingChanged)
        mantra?.addTarget(self, action: #selector(verifyUpdate(_:)), for: .editingChanged)
    }
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if let dest = segue.destination as? ProfilePicViewController{
            dest.source = self
        }
    }
    
    @IBAction func sendProfilePic(_ segue: UIStoryboardSegue) {
        if let pfpSegue = segue.source as? ProfilePicViewController {
            self.androidPfpRef = pfpSegue.androidPfpRef
            self.iosPfpRef = pfpSegue.iosPfpRef
            if self.iosPfpRef != nil {
                self.profilePic?.image = UIImage(named: iosPfpRef)
            }
            

            
        }
    }
    
    @IBAction func saveTapped(_ sender: Any) {
        if (!dataError) {
            updateUserData()
        }
        
    }
    
    override func touchesBegan(_ touches: Set<UITouch>, with event: UIEvent?) {
        email.endEditing(true)
        nickname.endEditing(true)
        firstName.endEditing(true)
        lastName.endEditing(true)
        mantra.endEditing(true)
    }
    
    func getUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
                    print(user)
                    if let email = user["email"] as? String {
                        print(email)
                        self.email?.text = email
                    }
                    if let username = user["nickname"] as? String {
                        print(username)
                        self.nickname?.text = username
                    }
                    if let firstname = user["firstName"] as? String {
                        print(firstname)
                        self.firstName?.text = firstname
                    }
                    if let lastname = user["lastName"] as? String {
                        print(lastname)
                        self.lastName?.text = lastname
                    }
                    if let mantra = user["mantra"] as? String {
                        print(mantra)
                        self.mantra?.text = mantra
                    }
                    if let birthdate = user["birthdate"] as? Timestamp {
                        let dateFormatter = DateFormatter()
                        dateFormatter.dateFormat = "MM/dd/yyyy"
                        let dateString = dateFormatter.string(from: birthdate.dateValue())
                        self.birthdate.text = dateString
                    }
                    
                    if let androidPfpRef = user["androidPfpRef"] as? Int {
                        self.androidPfpRef = androidPfpRef
                    }
                    
                    if let iosPfpRef = user["iosPfpRef"] as? String {
                        self.iosPfpRef = iosPfpRef
                        self.profilePic?.image = UIImage(named: iosPfpRef)
                    }
                    
                }
            } else {
                self.dataError = true
            }
        }
    }
    
    func updateUserData() {
        Auth.auth().currentUser?.updateEmail(to: email.text!) { error in
            if error != nil {
                self.error.text = "Unable to update user info, please try again later"
                self.error.isHidden = false
            }
            else {
                self.error.text = ""
                self.error.isHidden = true
                let db = Firestore.firestore()
                
                let params: [String: Any] = [
                    "nickname": self.nickname!.text,
                    "email": self.email!.text,
                    "firstName": self.firstName!.text,
                    "lastName": self.lastName!.text,
                    "mantra": self.getMantra(),
                    "androidPfpRef": self.androidPfpRef,
                    "iosPfpRef": self.iosPfpRef,
                ]
                
                db.collection("Adventourists")
                    .document(self.user.uid)
                    .updateData(params) { error in
                        if error == nil {
                            self.performSegue(withIdentifier: "unwindToProfile", sender: self)
                        }
                    }
            }
            
        }
        
    }
    
    func getMantra() -> String {
        if self.mantra!.text == "" {
            return "Tap the pencil icon to change your info!"
        } else {
            return self.mantra.text!
        }
    }
    
    func verifyNickname() -> Bool  {
        if nickname.text!.count > 16 || nickname.text! == ""{
            return false
        } else {
            return true
            
        }
    }
    
    func verifyFirstName() -> Bool  {
        
        let firstNamePattern = #"^[a-zA-Z]*$"#
        
        var result = firstName.text!.range(
            of: firstNamePattern,
            options: .regularExpression
        )
        
        let validFirstName = (result != nil)
        
        if (!validFirstName) {
            return false
        } else {
            return true
        }
        
    }
    
    func verifyLastName() -> Bool  {
        
        let lastNamePattern = #"^[a-zA-Z]*$"#
        
        var result = lastName.text!.range(
            of: lastNamePattern,
            options: .regularExpression
        )
        
        let validLastName = (result != nil)
        
        if (!validLastName) {
            return false
        } else {
            return true
            
        }
        
    }
    
    func verifyMantra() -> Bool  {
        if mantra.text!.count > 30 || mantra.text! == ""{
            return false
            
            
        } else {
            return true
        
            
            //verifyAlert()
            
        }
    }
    
    func verifyEmail() -> Bool {
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
            return false
        } else {
            return true
        }
    }
    
    @objc func verifyUpdate(_ textField: UITextField) {
        print("verify update")
        if !verifyEmail() {
            print("verify email")
            error.text = "Please enter a valid email"
            error.isHidden = false
            saveButton.isEnabled = false
            saveButton.backgroundColor = UIColor.lightGray
        }
        else if !verifyNickname() {
            print("verify nickname")
            error.text = "Nickname must be 16 characters or less."
            error.isHidden = false
            saveButton.isEnabled = false
            saveButton.backgroundColor = UIColor.lightGray
        }
        else if !verifyFirstName() {
            error.text = "First names must consist of only letters."
            error.isHidden = false
            saveButton.isEnabled = false
            saveButton.backgroundColor = UIColor.lightGray
        }
        else if !verifyLastName() {
            error.text = "Last names must consist of only letters."
            error.isHidden = false
            saveButton.isEnabled = false
            saveButton.backgroundColor = UIColor.lightGray
        }
        else if !verifyMantra() {
            error.text = "Mantra must be 30 characters or less."
            error.isHidden = false
            saveButton.isEnabled = false
            saveButton.backgroundColor = UIColor.lightGray
        }
        else {
            error.text = ""
            error.isHidden = true
            saveButton.isEnabled = true
            saveButton.backgroundColor = UIColor(named: "adv-royalblue")
        }
    }

}
