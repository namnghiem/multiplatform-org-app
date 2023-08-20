//
//  ExploreViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 01/09/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit

class ExploreViewController: UIViewController {
    
    @IBOutlet weak var stackView: UIStackView!
    
    @IBOutlet weak var view1: UIView!
    @IBOutlet weak var view2: UIView!
    @IBOutlet weak var view3: UIView!
    @IBOutlet weak var view4: UIView!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "Explore"
        // Do any additional setup after loading the view.
        view1.layer.cornerRadius = 17.0
        view2.layer.cornerRadius = 17.0
        view3.layer.cornerRadius = 17.0
        view4.layer.cornerRadius = 17.0
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
