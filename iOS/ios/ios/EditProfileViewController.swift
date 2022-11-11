//
//  EditProfileViewController.swift
//  ios
//
//  Created by Wanyea on 10/13/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class EditProfileViewController: UIViewController, ModalViewControllerDelegate {
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
    
    var dataError: Bool = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //profilePic?.image = picture
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        getUserData()
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
            self.performSegue(withIdentifier: "unwindToProfile", sender: self)
        } else {
            
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
        let db = Firestore.firestore()
        
        let params: [String: Any] = [
            "nickname": self.nickname!.text,
            "email": self.email!.text,
            "firstName": self.firstName!.text,
            "lastName": self.lastName!.text,
            "mantra": getMantra(),
            "androidPfpRef": self.androidPfpRef,
            "iosPfpRef": self.iosPfpRef,
        ]
        
        db.collection("Adventourists")
            .document(self.user.uid)
            .updateData(params)
    }
    
    func getMantra() -> String {
        if self.mantra!.text == "" {
            return "Tap the pencil icon to change your info!"
        } else {
            return self.mantra.text!
        }
    }
    
    func modalControllerWillDisappear(){
        
        //self.profilePic?.image = picture
        
        //signupData["iosPfpRef"] = String(profileChoice)
    }

}
