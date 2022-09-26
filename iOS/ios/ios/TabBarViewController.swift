//
//  TabBarViewController.swift
//  ios
//
//  Created by Glenn Hartwell on 9/8/22.
//

import UIKit
import FirebaseAuth

class TabBarViewController: UITabBarController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        Auth.auth().addStateDidChangeListener { auth, user in
            if let user = user {
                
            } else {
                self.switchToLogin()
            }
        }
        // Do any additional setup after loading the view.
    }

    
    func switchToLogin() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let mainTabBarController = storyboard.instantiateViewController(identifier: "LoginNavigationController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
    }

}
