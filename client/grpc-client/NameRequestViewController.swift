//
//  NameRequestViewController.swift
//  grpc-client
//
//  Created by Mo Lotfi on 9/3/17.
//  Copyright © 2017 MOIA GmbH. All rights reserved.
//

import UIKit

class NameRequestViewController: UIViewController {

  @IBOutlet weak var responseTextView: UITextView!
  @IBOutlet weak var usernameField: UITextField!

  private var requester: UnaryRPCManager?


  override func viewDidLoad() {
    requester = UnaryRPCManager(delegate: self)
    super.viewDidLoad()
  }

  override func didReceiveMemoryWarning() {
    super.didReceiveMemoryWarning()
  }

  @IBAction func requestButtonPressed(_ sender: Any) {
    let userId = usernameField.text != nil ? usernameField.text! : "Username is Empty"
    requester?.getUser(userId: userId)
    self.becomeFirstResponder()
  }
}


extension NameRequestViewController: UnaryRPCManagerDelegate {
  func unaryRPCManager(_ unaryManager: UnaryRPCManager, didUpdate response: Io_Moia_BookingResponse?, error: Error?) {
    self.responseTextView.text = response?.debugDescription
  }
}
