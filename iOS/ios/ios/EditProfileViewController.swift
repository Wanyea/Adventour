//
//  EditProfileViewController.swift
//  ios
//
//  Created by Wanyea on 10/13/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class EditProfileViewController: UIViewController {

    var user: User!
    
    @IBOutlet weak var email: UITextField!
    @IBOutlet weak var username: UITextField!
    @IBOutlet weak var firstname: UITextField!
    @IBOutlet weak var lastname: UITextField!
    @IBOutlet weak var mantra: UITextField!
    @IBOutlet weak var birthdaypicker: UIDatePicker!
    override func viewDidLoad() {
        super.viewDidLoad()
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        getUserData()
    }
    
    func getUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
                    print(user)
                    if let email = user["email"] as? String {
                        print(email)
                        self.email.text = email
                    }
                    if let username = user["nickname"] as? String {
                        print(username)
                        self.username.text = username
                    }
                    if let firstname = user["firstname"] as? String {
                        print(firstname)
                        self.firstname.text = firstname
                    }
                    if let lastname = user["lastname"] as? String {
                        print(lastname)
                        self.lastname.text = lastname
                    }
                    if let mantra = user["mantra"] as? String {
                        print(mantra)
                        self.mantra.text = mantra
                    }
                    if let birthday = user["birthday:"] as? String {
                        let dateFormatter = DateFormatter()
                        dateFormatter.dateFormat = "MM/dd/yyyy"
                        let formattedBirthday : NSDate = dateFormatter.date(from: birthday)! as NSDate
                        print(formattedBirthday)
                        self.birthdaypicker.setDate(formattedBirthday as Date, animated: false)
                    }
                    
                }
            }
        }
    }

}
