//
//  ProfileViewController.swift
//  ios
//
//  Created by Wanyea on 9/7/22.
//

import UIKit

class ProfileViewController: UIViewController {
    @IBOutlet weak var AdventourStack: UIStackView!
    @IBOutlet weak var BeaconStack: UIStackView!
    
    @IBOutlet weak var PassportView: UIView!
    @IBOutlet weak var AdventourView: UIView!
    @IBOutlet weak var BeaconBoardView: UIView!
    
    @IBOutlet weak var Username: UILabel!
    @IBOutlet weak var DOB: UILabel!
    @IBOutlet weak var ProfileDesc: UILabel!
    
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
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
