//
//  FinishSetupViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 02/09/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseDatabase
import FirebaseAuth

class FinishSetupViewController: UITableViewController, UITextFieldDelegate {
    @IBOutlet weak var finishButton: UIBarButtonItem!
    
    @IBOutlet weak var gradYear: UITextField!
    @IBOutlet weak var courseField: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        gradYear.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)
        courseField.addTarget(self, action: #selector(textFieldDidChange), for: .editingChanged)

        //self.navigationItem.rightBarButtonItem = UIBarButtonItem(title: "Finish", style: .done, target: self, action: #selector(YourViewController.yourAction))


        // Do any additional setup after loading the view.
    }
    
    @IBAction func onClick(_ sender: Any) {
        let ref = Database.database().reference()
        let auth = Auth.auth().currentUser!
        let info = ["username": auth.email!,"name":auth.displayName!,"position":"User","gradYear":gradYear.text!,"course":courseField.text!,"registered": false] as [String : Any]
        
        ref.child("users").child(Auth.auth().currentUser!.uid).setValue(info)
        
        dismiss(animated: true, completion: nil)
    }
    
    @objc func textFieldDidChange(sender: UITextField) {
        if !gradYear.text!.isEmpty && !courseField.text!.isEmpty{
            finishButton.isEnabled = true
        }else{
            finishButton.isEnabled = false
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
