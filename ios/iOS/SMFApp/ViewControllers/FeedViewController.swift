//
//  FeedViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 29/08/2019.
//  Copyright © 2019 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseUI

class FeedViewController: UIViewController, UITableViewDelegate, UITextFieldDelegate, FilterDelegate {
    func didPressFilter(_ tag: Int) {
        let alert = UIAlertController(title: "Report Content", message: "Hide content from your feed and report to the Society", preferredStyle: .actionSheet)
        alert.addAction(UIAlertAction(title: "Cancel", style: .default))
        
        alert.addAction(UIAlertAction(title: "Report", style: .destructive, handler: { (_) in
            print(self.dataSource.items)
            let current = self.dataSource.items[tag]
            self.filter.append(current.key)
            self.tableView.reloadRows(at: [IndexPath(row: tag, section: 0)], with: UITableView.RowAnimation.automatic)
            guard self.sectorId != nil else {return}
        self.ref.child("sectors").child(self.sectorId!).child("posts").child(current.key).updateChildValues(["reported":true])
        }))
        
        
        self.present(alert, animated: true, completion: nil)
        
    }
    
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        
        return dataSource.items.count
    }
    @IBOutlet weak var titleInput: UITextField!
    
    var dataSource: FUITableViewDataSource!
    var userId: String?
    var sectorId: String?
    var ref: DatabaseReference!
    var filter: [String] = []
    
    @IBOutlet weak var memberMask: UIView!
    @IBOutlet weak var tableView: UITableView!
    @IBOutlet weak var inputText: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.navigationItem.title = "Discussion Board"
        setupToHideKeyboardOnTapOnView()
        
        inputText.delegate = self
        titleInput.delegate = self
        tableView.allowsSelection = false
        ref = Database.database().reference()
        userId = Auth.auth().currentUser?.uid
        //get user
        ref.child("users").child(userId!).observeSingleEvent(of: .value, with: { (snapshot) in
            
            let value = snapshot.value as? NSDictionary
            
            self.sectorId = value?["sector"] as? String
            if self.sectorId != nil{
                self.memberMask.isHidden = true
                //if registered analyst

                self.dataSource = self.tableView.bind(to: self.ref.child("sectors").child(self.sectorId!).child("posts").queryOrdered(byChild: "order")) {tableView, indexPath, snap in
                    
                    
                    
                    let snapshot = (snap.value as? NSDictionary)
                    print(snapshot!)
                    let cell = tableView.dequeueReusableCell(withIdentifier: "feedId", for: indexPath) as! FeedTableViewCell
                    // Populate cell as you see fit, like as below
                    
                    let name = "Posted by: " + (snapshot!["authorName"] as? String ?? "Author")
                    
                    cell.authorLabel.text = name
                    cell.postLabel.text = "Posted on: " + (snapshot!["time"] as? String ?? "Recently")
                    
                    //cell.contentCell.layer.shadowOffset = CGSize(width: 0, height: 0)
                    //cell.contentCell.layer.shadowColor = UIColor.black.cgColor
                    //cell.contentCell.layer.shadowRadius = 8
                    //cell.contentCell.layer.shadowOpacity = 0.25
                    //cell.contentCell.layer.masksToBounds = false;
                    cell.contentCell.layer.cornerRadius = 17.0
                    //cell.contentCell.clipsToBounds = false;
                    cell.descriptionLabel.text = snapshot!["main"] as? String ?? "No post found"
                    cell.titleLabel.text = snapshot!["title"] as? String ?? "Memo"
                    tableView.rowHeight = 200
                    
                    tableView.delegate = self
                    
                    if self.filter.contains(snap.key){
                        cell.filterMask.isHidden = false
                    }else{
                        cell.filterMask.isHidden = true
                    }
                    cell.bringSubviewToFront(cell.filterButton)
                    cell.delegate = self
                    cell.filterButton.tag = indexPath.row
                    
                    
                    return cell
                }


            }else{
                //if unregistered]
                
                self.memberMask.isHidden = false
            }
        })
        // Do any additional setup after loading the view.
        //reverse
   

    }

    @IBAction func onClick(_ sender: Any) {
        guard let input = inputText.text else{return};
        let dateFormat = DateFormatter()
        dateFormat.dateFormat = "YYYY-MM-dd hh:mm:ss"
        
        let time = dateFormat.string(from: Date())
        
        let title = titleInput.text ?? "Memo"
        let name = Auth.auth().currentUser?.displayName
        let postInfo = ["authorId": userId!,"authorName":name!,"main":input,"order": 0-Date().timeIntervalSince1970,"time": time,"title": title] as [String : Any]
    
        let reference  = ref.child("sectors").child(sectorId!).child("posts").childByAutoId()
        reference.setValue(postInfo)
        self.view.endEditing(true)
        
        inputText.text = ""
        titleInput.text = ""
    }
    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destination.
        // Pass the selected object to the new view controller.
    }
    */
    /**
     * Called when 'return' key pressed. return NO to ignore.
     */
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    
    

}

extension UIViewController
{
    func setupToHideKeyboardOnTapOnView()
    {
        let tap: UITapGestureRecognizer = UITapGestureRecognizer(
            target: self,
            action: #selector(UIViewController.dismissKeyboard))
        
        tap.cancelsTouchesInView = false
        view.addGestureRecognizer(tap)
    }
    
    @objc func dismissKeyboard()
    {
        view.endEditing(true)
    }
}
