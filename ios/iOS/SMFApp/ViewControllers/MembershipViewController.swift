//
//  MembershipViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 29/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseAuth
import QRCode
class MembershipViewController: UIViewController {

    @IBOutlet weak var qrImage: UIImageView!
    @IBOutlet weak var doneButton: UIBarButtonItem!
    @IBOutlet weak var timeLabel: UILabel!
    @IBOutlet weak var cardLayout: UIView!
    
    @IBOutlet weak var stackView: UIStackView!
    
    @IBOutlet weak var errorLayout: UIView!
    
    
    var timer = Timer()

    override func viewDidLoad() {
        super.viewDidLoad()
    self.navigationController?.navigationBar.prefersLargeTitles = true
        navigationController?.navigationItem.largeTitleDisplayMode = .automatic

        timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector:#selector(self.tick) , userInfo: nil, repeats: true)

        
        let id = String(Auth.auth().currentUser!.uid)
        let qrCode = QRCode(id)
        qrImage.image = qrCode?.image

        cardLayout.layer.cornerRadius = 17.0
        
    }

        // Do any additional setup after loading the view.
    
    
    @objc func tick() {
        timeLabel.text = DateFormatter.localizedString(from: Date(),
                                                              dateStyle: .medium,
                                                              timeStyle: .medium)
    }
    
    @IBAction func onClick(_ sender: UIBarButtonItem) {
        dismiss(animated: true, completion: nil)

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
