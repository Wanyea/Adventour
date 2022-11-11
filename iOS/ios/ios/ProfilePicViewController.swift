//
//  ProfilePicViewController.swift
//  ios
//
//  Created by Wanyea on 10/31/22.
//

import UIKit

class ProfilePicViewController: UIViewController {
    var source: UIViewController!
    var androidPfpRef: Int!
    var iosPfpRef: String!
    
    weak var delegate: ModalViewControllerDelegate?
    
    @IBOutlet weak var profileCheeta: ProfilePicImageView!
    @IBOutlet weak var profileElephant: ProfilePicImageView!
    @IBOutlet weak var profileFox: ProfilePicImageView!
    
    @IBOutlet weak var profileBug: ProfilePicImageView!
    @IBOutlet weak var profileMonkey: ProfilePicImageView!
    @IBOutlet weak var profilePenguin: ProfilePicImageView!
    
    

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        let tap = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap2 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap3 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap4 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap5 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        let tap6 = UITapGestureRecognizer(target: self, action: #selector(self.tapDetected))
        profileCheeta?.isUserInteractionEnabled = true
        profileCheeta?.iosPfpRef = "profpic_cheetah"
        profileCheeta?.androidPfpRef = 0
        profileCheeta?.addGestureRecognizer(tap)
        profileElephant?.isUserInteractionEnabled = true
        profileElephant?.iosPfpRef = "profpic_elephant"
        profileElephant?.androidPfpRef = 1
        profileElephant?.addGestureRecognizer(tap2)
        profileFox?.isUserInteractionEnabled = true
        profileFox?.iosPfpRef = "profpic_fox"
        profileFox?.androidPfpRef = 4
        profileFox?.addGestureRecognizer(tap3)
        profileBug?.isUserInteractionEnabled = true
        profileBug?.iosPfpRef = "profpic_ladybug"
        profileBug?.androidPfpRef = 2
        profileBug?.addGestureRecognizer(tap4)
        profileMonkey?.isUserInteractionEnabled = true
        profileMonkey?.iosPfpRef = "profpic_monkey"
        profileMonkey?.androidPfpRef = 3
        profileMonkey?.addGestureRecognizer(tap5)
        profilePenguin?.isUserInteractionEnabled = true
        profilePenguin?.iosPfpRef = "profpic_penguin"
        profilePenguin?.androidPfpRef = 5
        profilePenguin?.addGestureRecognizer(tap6)
        
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        self.performSegue(withIdentifier: "sendProfilePic", sender: self)
    }
    
    @objc func tapDetected(sender: UITapGestureRecognizer){
        
        //picture = self.image
        
        let view = sender.view as! ProfilePicImageView
        signupData["iosPfpRef"] = view.iosPfpRef
        signupData["androidPfpRef"] = view.androidPfpRef
        self.iosPfpRef = view.iosPfpRef
        self.androidPfpRef = view.androidPfpRef
        
        picture = view.image
        picFlag = 1
        if let delegate = delegate {
            delegate.modalControllerWillDisappear()
        }
        self.dismiss(animated: true, completion: nil)

    }
    
    
    
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        print("prepare check")
        if let destinationVC = segue.destination as? SignUpViewController{
            print("1")
            if(picFlag == 0)
                    {
                destinationVC.profilePic?.image = UIImage(systemName: "questionmark")
                    }
            else{
                destinationVC.profilePic?.image = picture
            }
            
        
        }
        
        if let destinationVC = segue.destination as? EditProfileViewController{
            if signupData["androidPfpRef"] != nil {
                destinationVC.androidPfpRef = (signupData["androidPfpRef"] as! Int)
            }
            if signupData["iosPfpRef"] != nil {
                destinationVC.iosPfpRef = (signupData["iosPfpRef"] as! String)
            }
            
            }
        
        
        
    }
    
    
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    

}

protocol ModalViewControllerDelegate: NSObjectProtocol{
    func modalControllerWillDisappear()
}

final class ModalViewController: UIViewController{
    weak var delegate: ModalViewControllerDelegate?
}

