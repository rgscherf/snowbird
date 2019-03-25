(ns snowbird.analysis.core
  (:require [snowbird.analysis.pmd :as pmd]
            [snowbird.specs.core :as specs]
            [snowbird.input.file-system :as fs]
            [clojure.spec.alpha :as s]
            [snowbird.utils.core :as utils])
  (:import java.util.Date
           java.util.UUID))

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

(defn resolve-rule-name
  "Resolve rule's name from rule NS."
  [rule-ns]
  (-> rule-ns (ns-resolve 'rule-name) deref))


(defn resolve-rule-fn
  "Resolve rule's run fn from NS symbol."
  [rule-ns]
  (ns-resolve rule-ns 'run))


(defn run-custom-rules
  "Resolve rule fns from seq of rule symbols, and run them against file paths."
  [rule-syms file-paths]
  (flatten
    ((apply juxt (map resolve-rule-fn rule-syms)) file-paths)))


(defn analyze
  [file-paths config]
  {:pre [(s/assert (s/coll-of ::specs/file-path) file-paths)
         (s/assert ::specs/config config)]
   :post [(s/assert ::specs/analysis-result %)]}
  (let [analysis-date (Date.)
        id (UUID/randomUUID)]
    {:analysis-time analysis-date
     :id            id
     :config        config
     :results       (apply merge
                           (for [t (:file-types config)]
                             {t {:files-examined
                                   (map utils/name-from-path file-paths)
                                 :rules
                                   (sort
                                     (concat
                                       (map resolve-rule-name
                                            (-> config :custom-rules t))
                                       (fs/pmd-xml->rule-names (-> config
                                                                   :pmd-rules
                                                                   t))))
                                 :violations
                                   (map #(assoc % :analysis-id id)
                                        (concat
                                          (run-custom-rules (-> config :custom-rules t)
                                                            file-paths)
                                          (pmd/violation-seq file-paths
                                                             t
                                                             config)))}}))}))

#_(analyze files (fs/read-config-file))
