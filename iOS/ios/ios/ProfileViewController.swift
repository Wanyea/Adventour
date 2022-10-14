//
//  ProfileViewController.swift
//  ios
//
//  Created by Wanyea on 9/7/22.
//

import UIKit
import FirebaseAuth
import FirebaseFirestore

class ProfileViewController: UIViewController {
    
    var user: User!
    
    @IBOutlet weak var username: UILabel!
    @IBOutlet weak var birthday: UILabel!
    @IBOutlet weak var mantra: UILabel!
    override func viewDidLoad() {
        super.viewDidLoad()
        if let user = Auth.auth().currentUser {
            self.user = user
        }
        getUserData()
        // Do any additional setup after loading the view.
    }
    
    func getUserData() {
        let db = Firestore.firestore()
        db.collection("Adventourists").document(self.user.uid).getDocument { document, error in
            if let document = document, document.exists {
                document.data().map { user in
                    print(user)
                    if let birthday = user["birthday:"] as? String {
                        print(birthday)
                        self.birthday.text = birthday
                    }
                    if let username = user["nickname"] as? String {
                        print(username)
                        self.username.text = username
                    }
                    if let mantra = user["mantra"] as? String {
                        print(mantra)
                        self.mantra.text = mantra
                    }
                    
                }
            }
        }
    }

}
