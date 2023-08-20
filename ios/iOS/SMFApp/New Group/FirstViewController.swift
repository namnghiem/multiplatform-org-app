//
//  FirstViewController.swift
//  SMFApp
//
//  Created by Nam Nghiem on 23/09/2018.
//  Copyright © 2018 Nam Nghiem. All rights reserved.
//

import UIKit
import FirebaseAuth
import FirebaseUI
import Firebase
import Alamofire
import SafariServices
import QRCodeReader
import AVFoundation
import SwiftyJSON

class FirstViewController: UIViewController, FUIAuthDelegate, UITableViewDelegate, QRCodeReaderViewControllerDelegate {
    
    
    
    struct StockData {
        var buyPrice: NSInteger
        var number: NSInteger
        var ticker: String
        var title: String
    }
    var reachability: Reachability?
    @IBOutlet weak var barcodeIcon: UIBarButtonItem!
    
    @IBOutlet  var meetingLabel: UILabel!
    @IBOutlet  var tableView: UITableView!
    
    @IBOutlet  var headerView: UIStackView!
    
    @IBOutlet  var profileCard: UIView!
    @IBOutlet  var profileBackground: UIImageView!
    @IBOutlet  var navigationBar: UINavigationBar!
    @IBOutlet  var profilePicture: UIImageView!
    @IBOutlet  var sectorCard: UIView!
    @IBOutlet  var managerProfile: UIImageView!
    @IBOutlet  var eventsCard: UIView!

    @IBOutlet  var sectorProfileLabel: UILabel!
    @IBOutlet  var nameLabel: UILabel!
    @IBOutlet  var positionLabel: UILabel!
    @IBOutlet  var scrollView: UIScrollView!
    
    @IBOutlet  var sectorLabel: UILabel!
    @IBOutlet  var activityIndicator: UIActivityIndicatorView!
    
    
    @IBOutlet  var managerName: UILabel!
    @IBOutlet  var managerTitle: UILabel!
    @IBOutlet  var managerEmail: UILabel!
    @IBOutlet  var managerImage: UIImageView!
    
    @IBOutlet  var eventsLabel: UILabel!
    @IBOutlet  var eventsButton: UIButton!
    @IBOutlet  var eventsImage: UIImageView!
    
    var sectorCode: String?
    
    var loginAttempt = false
    
    var eventsUrl: String!
    
    var stockData: [StockData] = []
    var dataSource: FUITableViewDataSource!
    
    var priceList: [String:Double] = [:]
    var stockList: [String] = []
    
    var ref: DatabaseReference?
    
    var weekdays: [String:Int] = ["Sunday":1, "Monday":2, "Tuesday":3, "Wednesday":4, "Thursday":5, "Friday":6, "Saturday":7]
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        if let index = self.tableView.indexPathForSelectedRow{
            self.tableView.deselectRow(at: index, animated: true)
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        
        do{
            reachability = try Reachability()
            
            reachability!.whenUnreachable = { _ in
                print("Not reachable")
                let alert = UIAlertController(title: "No internet connection", message: "Please check your WI-FI or 4G connection to continue", preferredStyle: .alert)
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
                self.present(alert, animated: true, completion: nil)
                
            }
            reachability!.whenReachable = { _ in
                if Auth.auth().currentUser != nil {
                    
                    self.load()

                }
            }
            do {
                try reachability!.startNotifier()
            } catch {
                print("Unable to start notifier")
            }
        }catch let exception{
            
        }

        
        cardSetup(view: profileCard)
        cardSetup(view: sectorCard)
        cardSetup(view: eventsCard)
        
        headerView.autoresizingMask = []
        
        self.navigationController?.navigationItem.largeTitleDisplayMode = .always
        self.navigationController?.navigationBar.prefersLargeTitles = true
        self.title = "Trinity SMF"
        profilePicture.layer.cornerRadius = profilePicture.frame.size.width/2
        managerProfile.layer.cornerRadius = managerProfile.frame.size.width/2
        activityIndicator.hidesWhenStopped = true
        activityIndicator.startAnimating()
        
        if Auth.auth().currentUser != nil {

            load()
        
        }    }
    
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        
        if Auth.auth().currentUser == nil{
            
            login()
        }else if loginAttempt{
            load()
            loginAttempt = false
        }
    }
    
    func login(){
        
        let authUI = FUIAuth.defaultAuthUI()
        authUI?.delegate = self
        
        let providers: [FUIAuthProvider] = [FUIEmailAuth()]
        authUI?.providers = providers
        authUI?.privacyPolicyURL = URL( string: "http://www.trinitysmf.com/privacy-policy/")
        authUI?.tosurl = URL(string: "http://www.trinitysmf.com/terms-of-service/")
        let authViewController = CustomAuthPickerViewController(authUI:authUI!)
        
        let navc = UINavigationController(rootViewController:authViewController)
        self.present(navc, animated: true, completion: {
            
        })
        
        loginAttempt = true
        
    }
    
    func finishSignup(){
        let vc = FinishSetupViewController() //change this to your class name
        performSegue(withIdentifier: "modalSegue", sender: self)
        loginAttempt = true
    }
    func load(){
        edgesForExtendedLayout = .all
        extendedLayoutIncludesOpaqueBars = true
        
        //let containerView = UIView()
        //containerView.translatesAutoresizingMaskIntoConstraints = false
        // headerView is your actual content.
        // containerView.addSubview(headerView)
        AF.request("http://trinitysmf.com/wp-json/wp/v2/posts?_embed&per_page=100&categories=7", method: .get, parameters: nil, encoding: JSONEncoding.default)
            .responseJSON { response in
                //to get status code
                if let status = response.response?.statusCode {
                    switch(status){
                    case 201:
                        print("example success")
                    default:
                        print("error with response status: \(status)")
                    }
                }
                //to get JSON return value
                do{
                    guard response.data != nil else {return
                        //handle offline
                    }
                    let json =  JSON(response.data!)
                    
                    let title = json[0]["title"]["rendered"].string ?? "No events"
                    let eventUrl = json[0]["link"].string ?? "No events"
                    let imageUrl = json[0]["_embedded"]["wp:featuredmedia"][0]["media_details"]["sizes"]["large"]["source_url"].string ?? "http://www.trinitysmf.com/wp-content/uploads/2018/07/educlass_series-1024x682.jpg"
                    //Now get title value
                    
                    self.eventsLabel.text = title
                    self.eventsUrl = eventUrl
                    
                
                    if let url = URL(string: imageUrl){
                        self.eventsImage.load(url: url)
                        print(url)
                    }
                }catch let parsingError {
                    print("Error", parsingError)
                }
                
        }
        
            //do something :D
        ref = Database.database().reference()
            
        let userID = Auth.auth().currentUser?.uid
            
            
            //set name
        ref!.child("users").child(userID!).observeSingleEvent(of: .value, with: { (snapshot) in
            // Get user value
            let value = snapshot.value as? NSDictionary
            
            if value != nil{
            //CHECK FINISHED registration
                if (!snapshot.exists()){
                    self.finishSignup()
                    return
                }
                
                
                self.nameLabel.text = value?["name"] as? String ?? ""
                self.positionLabel.text = value?["position"] as? String ?? ""
                
                if let profileString = value?["pictureUrl"] as? String{
                    if let profileUrl = URL(string: profileString){
                        self.profilePicture!.load(url: profileUrl)
                    }
                }
                
                //get sector
                //if analyst (there is sector value), get sector
                if value!["sector"] != nil {
                    if let sectorId = value!["sector"] as? String{
                        self.setupAnalyst(value: value!)
                    }else{
                        self.setupMember()
                    }
                }else{
                    self.setupMember()
                }
            }else{
                self.finishSignup()
            }
        })
    }
    
    func setupMember(){
        self.activityIndicator.stopAnimating()
        self.tableView.isHidden = false
        sectorCard.isHidden = true
        barcodeIcon.isEnabled = false
        sectorLabel.text = ""
        //profilePicture.image = UIImage(contentsOfFile: "02th-egg-person.jpeg")
        managerImage.image = UIImage(contentsOfFile: "02th-egg-person.jpeg")
        
        
    }
    
    func setupAnalyst(value: NSDictionary){
        
        sectorCard.isHidden = false
        
        if let sectorId = value["sector"] as? String{
            sectorCode = sectorId
            //get portfolio
            self.dataSource = self.tableView.bind(to: (self.ref?.child("sectors").child(sectorId).child("portfolio"))!) { tableView, indexPath, snap in
                let snapshot = snap.value as? NSDictionary
                let cell = tableView.dequeueReusableCell(withIdentifier: "stockId", for: indexPath) as! StockCellTableViewCell
                // Populate cell as you see fit, like as below
                
                let ticker = snapshot?["ticker"] as? String ?? "Fail"
                
                cell.stockTicker.text = ticker
                cell.stockName.text = snapshot!["title"] as? String ?? "Fail"
                
                let buyPrice = snapshot?["buyPrice"] as? Double ?? 1
                let buyNumber = snapshot?["number"] as? Int ?? 1
                
                cell.stockName.text = (snapshot!["title"] as? String ?? "Fail") + " (x" + String(buyNumber) + ")"

                tableView.rowHeight = 130
                
                tableView.delegate = self
                
                if self.priceList[ticker] != nil {
                    let price = round(self.priceList[ticker] ?? 1)*100/100
                    cell.stockBuyPrice.text = price.description
                    
                    let change = round((price - buyPrice)/buyPrice*100*100)/100
                    cell.stockNumber.text = String("Buy price: \(buyPrice)" + "(\(change)%)")
                    
                }else{
                AF.request("https://www.alphavantage.co/query?function=TIME_SERIES_INTRADAY&symbol="+ticker+"&interval=15min&outputsize=compact&apikey=63SO9QGJRKSX7WTD", method: .get, parameters: nil, encoding: JSONEncoding.default)
                    .responseJSON { response in
                        //to get JSON return value
                        do{
                            guard response.data != nil else {return}
                            guard let json = try? JSONSerialization.jsonObject(with: response.data!) else {return}
                            if let json = try? JSONSerialization.jsonObject(with: response.data!, options: []){
                                //let json = try? JSONSerialization.jsonObject(with: response.data!, options: [])
                                
                                guard let jsonArray = json as? [String: Any] else {
                                    return
                                }
                                //Now get title value
                                guard let timeSeries = jsonArray["Time Series (15min)"] as? [String:Any] else {
                                    return
                                    
                                }
                                
                                let key = timeSeries.first!.key
                                guard let recent = timeSeries[key] as? [String:Any] else{
                                    return
                                    
                                }
                                
                                
                                self.priceList[ticker] = Double(recent["4. close"] as! String)
                                self.stockList.append(ticker)
                                
                                cell.stockBuyPrice.text = (round(self.priceList[ticker]!*100)/100).description
                                
                                let price = self.priceList[ticker]
                                let change = round((price! - buyPrice)/buyPrice*100*100)/100
                                cell.stockNumber.text = String("Buy price: \(buyPrice) " + "(\(change))")
                            }
                            
                        }catch let parsingError {
                            print("Error", parsingError)
                        }
                            
                    }
                }
                
                
                
                return cell
            }
            
            self.ref?.child("sectors").child(sectorId).observeSingleEvent(of: .value, with: { (sectorSnapshot) in
                let sectorValue = sectorSnapshot.value as? NSDictionary
                
                
                
                self.sectorLabel.text = sectorValue?["name"] as? String ?? ""
                
                self.sectorProfileLabel.text = sectorValue?["name"] as? String ?? ""
                
                
                //get closest sector meeting
                
                let meetingsArray = sectorValue?["weekly_meetings"]as? [String:Any]
                
                
                let calendar = Calendar(identifier: .gregorian)
                
                var closestDate: Date = Date(timeIntervalSince1970: 0);
                
                var shortestTime: TimeInterval = 0
                if meetingsArray != nil{
                for child in meetingsArray!{
                    let childArray = child.value as! [String:Any]
                    
                    let current = Meeting(day: childArray["day"] as! String, time: childArray["time"] as! String, venue: childArray["venue"] as! String)
                    
                    let currentDayArray = current.time.components(separatedBy: ":")
                    
                    if let weekdayInt = self.weekdays[current.day]{
                        let currentComponent = DateComponents(calendar: calendar, hour:Int(currentDayArray[0]), minute:Int(currentDayArray[1]), weekday:weekdayInt)
                        let nextDay = calendar.nextDate(after: Date(), matching: currentComponent, matchingPolicy: .nextTime)
                        
                        let timeSince = nextDay?.timeIntervalSinceNow
                        
                        if shortestTime == 0 || timeSince! < shortestTime{
                            closestDate = nextDay!
                            shortestTime = timeSince!
                        }
                    }
                    
                }
            
                let formatter = DateFormatter()
                formatter.locale = Locale(identifier: "en_US_POSIX")
                formatter.dateFormat = "EEEE, dd/MM/yyyy HH:mm"
                
                self.meetingLabel.text = formatter.string(from:closestDate)
                
                }else{
                    self.meetingLabel.text = "No meetings scheduled"
                }
                
                
                //get manager
                
                if let managerId = sectorValue?["sector_manager"] as? String{
                    
                    
                    //get sector
                    self.ref!.child("users").child(managerId).observeSingleEvent(of: .value, with: { (managerSnapshot) in
                        let managerValue = managerSnapshot.value as? NSDictionary
                        self.managerName.text = managerValue?["name"] as? String ?? ""
                        self.managerTitle.text = managerValue?["position"] as? String ?? ""
                        self.managerEmail.text = managerValue?["username"] as? String ?? ""
                        
                        if let managerImageUrl = URL(string:(managerValue?["pictureUrl"]as? String ?? "")), managerValue!["pictureUrl"] != nil {
                            self.managerImage.load(url: managerImageUrl)
                        }else{
                            //
                        }
                        
                        
                        self.activityIndicator.stopAnimating()
                        self.tableView.isHidden = false
                        
                        if managerId == Auth.auth().currentUser?.uid{
                            self.barcodeIcon.isEnabled = true
                            self.managerTitle.text = "You are the Sector Manager"
                            
                        }
                        
                        
                    })
                    
                    
                    
                    
                    if managerId == Auth.auth().currentUser?.uid{
                        self.barcodeIcon.isEnabled = true
                        self.managerEmail.text = "You are the Sector Manager"
                        
                    }
                    
                    
                }
                
            })
        
        
        
        
        
        
        }
        
        
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        print("row: \(indexPath.row)")
        guard let url = URL(string: "https://finance.yahoo.com/quote/" + stockList[indexPath.row]) else { return }
        let svc = SFSafariViewController(url: url)
        present(svc, animated: true, completion: nil)
        
    }
    

    func cardSetup(view: UIView){
        view.layer.cornerRadius = 16.0
    }
    
    // Swift
    func authPickerViewController(forAuthUI: FUIAuth) -> FUIAuthPickerViewController {
        return CustomAuthPickerViewController(authUI: forAuthUI)
    }

    @IBAction func onClick(_ sender: UIButton) {
        if eventsUrl != nil{
        guard let url = URL(string: eventsUrl) else { return }
        let svc = SFSafariViewController(url: url)
        present(svc, animated: true, completion: nil)
        }

    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.items.count
    }
    
    //qr
    
    lazy var readerVC: QRCodeReaderViewController = {
        let builder = QRCodeReaderViewControllerBuilder {
            $0.reader = QRCodeReader(metadataObjectTypes: [.qr], captureDevicePosition: .back)
            
            // Configure the view controller (optional)
            $0.showTorchButton        = false
            $0.showSwitchCameraButton = false
            $0.showCancelButton       = true
            $0.showOverlayView        = true
            $0.rectOfInterest         = CGRect(x: 0.2, y: 0.2, width: 0.6, height: 0.6)
        }
        
        return QRCodeReaderViewController(builder: builder)
    }()



    
    @IBAction func onQrCode(_ sender: Any) {
        // Retrieve the QRCode content
        // By using the delegate pattern
        readerVC.delegate = self
        
        // Or by using the closure pattern
        readerVC.completionBlock = { (result: QRCodeReaderResult?) in
            print(result?.value)
        }
        
        // Presents the readerVC as modal form sheet
        readerVC.modalPresentationStyle = .formSheet
        
        present(readerVC, animated: true, completion: nil)
    }
    
    func reader(_ reader: QRCodeReaderViewController, didScanResult result: QRCodeReaderResult) {
        reader.stopScanning()
        print(result)
        
        dismiss(animated: true, completion: nil)
        
        guard sectorCode != nil else {return}
        
        ref?.child("users").child(result.value).observeSingleEvent(of: .value, with: {(snapshot) in
            
            if(snapshot.exists()){
                self.ref?.child("users").child(result.value).child("sector").setValue(self.sectorCode!)
                let alert = UIAlertController(title: "User Registration", message: "Successfully added user to Sector", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
                
                self.present(alert, animated: true)
            }else{
                let alert = UIAlertController(title: "Registration Issue", message: "There was an issue with this digital ID.", preferredStyle: .alert)
                
                alert.addAction(UIAlertAction(title: "OK", style: .default, handler: nil))
                
                self.present(alert, animated: true)
            }
        })
        //if exists)
        
    }
    func readerDidCancel(_ reader: QRCodeReaderViewController) {
        dismiss(animated: true, completion: nil)
    }
    
    @IBAction func logoutButton(_ sender: Any) {
        do{
            try Auth.auth().signOut()
            login()
            loginAttempt = true
            dataSource = nil
            tableView.reloadData()
        }catch let exception{
            print(exception)
        }
    }
}




extension UIImageView {
    func load(url: URL) {
        DispatchQueue.global().async { [weak self] in
            if let data = try? Data(contentsOf: url) {
                if let image = UIImage(data: data) {
                    DispatchQueue.main.async {
                        self?.image = image
                    }
                }
            }
        }
    }
}
