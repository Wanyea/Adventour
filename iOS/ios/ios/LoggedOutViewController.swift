//
//  ViewController.swift
//  storyboard
//
//  Created by Cassidy Moore on 8/23/22.
//

import UIKit
import FirebaseAuth

class LoggedOutViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
//        Auth.auth().addStateDidChangeListener { auth, user in
//            if let user = user {
//                self.switchToTabController()
//            } else {
//                
//                
//            }
//        }
        // Do any additional setup after loading the view.
    }


    func switchToTabController() {
        let storyboard = UIStoryboard(name: "Main", bundle: nil)
       
        let mainTabBarController = storyboard.instantiateViewController(identifier: "TabBarViewController")
        
        // This is to get the SceneDelegate object from your view controller
        // then call the change root view controller function to change to main tab bar
        (UIApplication.shared.connectedScenes.first?.delegate as? SceneDelegate)?.changeRootViewController(mainTabBarController)
    }
}

