(ns snowbird.custom-rules.apex.no-dml-in-controller
  (:require [clojure.java.io :as io]
            [clojure.string :as string]
            [snowbird.utils.core :as utils]))

(def dml-regex #"\s+(insert|delete|upsert|update)\s+")
(def allowed-file-pattern-regex #"(model_|test\.cls|testfactory\.cls)")
(def rule-name "NoDmlInController")

(defn- run-single
  [file-path]
  (with-open [f (io/reader file-path)]
    (-> (->> (line-seq f)
             (map (fn [n l] {:line n
                             :file-path file-path
                             :file-name (utils/name-from-path file-path)
                             :rule rule-name
                             :description (str " " (string/trim l) " ")})
                             ;; ^^ so that we still catch DML as first token
                  (range 15000))
             (filter #(re-find dml-regex (:description %))))
        doall
        seq)))

(defn run
  [file-paths]
  (->> file-paths
       (remove #(re-find allowed-file-pattern-regex (string/lower-case %)))
       (map run-single)
       (remove nil?)
       flatten))


(def files
  [ "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_ServiceLayerUtil_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContacts_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/ChangePasswordController.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/AssetTrigger_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_Asset.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Scheduled_Batch_GISAddressUpdateClass.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_OrderItem_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_OutcomeException.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/AccountContactTrigger_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/telusComWsdlRmoOrdermgmtEquipmentre.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/TestFactory_Knock.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_WorkflowTemplate_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/TriggerHandler_SMS_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_OutcomeLiteTest.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/xmlschemaTmiTelusComXsdEnterpriseB.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/CreateCaseFromEmail.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_RecordsDeleteClass_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_GeopointeCreateLead.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_Task_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_GenericEventMetadata.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/CommunitiesLoginController.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/RetrieveNextUtils_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_BillingData.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_DPOBulkUpdate_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContactwithAccountId.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_DropAccountMemberRole_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_DeleteOldBillingData_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_RemoveOrphanCustomerAddress_Test.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/MyProfilePageController.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_OutcomeContactUtil.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_SMS_JSON.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_ContactWorkOrderDisplay.cls"
   "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_MyProfilePage.cls"])
