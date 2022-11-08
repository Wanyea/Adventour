//
//  PassportHamburgerMenuViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 10/27/22.
//

import UIKit
import FirebaseAuth

class PassportHamburgerMenuViewController: UIViewController {

    @IBOutlet weak var signOutButton: UIButton!
    @IBOutlet weak var exitIndicator: UIView!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Do any additional setup after loading the view.
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(true)
        self.exitIndicator?.layer.cornerRadius = 3
    }
    
    @IBAction func signOutUser(_ sender: Any) {
        let firebaseAuth = Auth.auth()
        
        let alert = UIAlertController(
            title: "Sign out of Adventour?",
            message: "If you have any Adventours in progress they will be lost! Are you sure you want to sign out?",
            preferredStyle: .actionSheet
        )
        alert.addAction(UIAlertAction(
            title: "Sign Out",
            style: .destructive,
            handler: { _ in
                do {
                    try firebaseAuth.signOut()
                    self.resetFilters()
                    self.switchToLoggedOut()
                } catch let signOutError as NSError {
                  print("Error signing out: %@", signOutError)
                }
                
        }))
        alert.addAction(UIAlertAction(
            title: "Cancel",
            style: .cancel,
            handler: { _ in

        }))
        present(alert,
                animated: true,
                completion: nil
        )
        
        
    }
    
    func switchToLoggedOut() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let loggedOutVc = storyboard.instantiateViewController(identifier: "LoginNavigationController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(loggedOutVc)
    }
    
    func resetFilters() {
        UserDefaults.standard.removeObject(forKey: "socialSwitch")
        UserDefaults.standard.removeObject(forKey: "outdoorsySwitch")
        UserDefaults.standard.removeObject(forKey: "cultureSwitch")
        UserDefaults.standard.removeObject(forKey: "hungrySwitch")
        UserDefaults.standard.removeObject(forKey: "romanticSwitch")
        UserDefaults.standard.removeObject(forKey: "geekySwitch")
        UserDefaults.standard.removeObject(forKey: "spirtualSwitch")
        UserDefaults.standard.removeObject(forKey: "sportySwitch")
        UserDefaults.standard.removeObject(forKey: "chillSwitch")
        UserDefaults.standard.removeObject(forKey: "shoppySwitch")
        UserDefaults.standard.removeObject(forKey: "pamperedSwitch")
        UserDefaults.standard.removeObject(forKey: "distanceSlider")
    }

}
