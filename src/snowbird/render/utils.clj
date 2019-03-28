(ns snowbird.render.utils
  (:require [snowbird.specs.core :as specs]
            [clojure.spec.alpha :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; MAKING VIOLATION SEQS INTO MAPS

(defn transpose-by
  "Transpose a CSV by some key. A CSV is a sequence of maps.
  Result is a map of transposition-key to all rows matching that key.
  Optional arg `keyfn` will be applied to the resultant key."
  [transposition-key input-map & {:keys [keyfn] :or {keyfn identity}}]
  (reduce (fn [acc-map input-row]
            (update acc-map
                    (keyfn (get input-row transposition-key))
                    conj
                    input-row))
          {}
          input-map))

(defn transpose-to-violation-map
  "Given a CSV (seq of violation-maps), create a map of
  {:file => {:violated-rule [violation-maps]}
  File names are string keys, violated rules are kw keys."
  [csv-output]
  {:post [(s/assert ::specs/violation-map %)]}
  (let [transposed-by-file (transpose-by :file-name csv-output)]
    (reduce-kv (fn [m k v]
                 (assoc m k (transpose-by :rule v :keyfn keyword)))
               {}
               transposed-by-file)))

(def testinput
  '{:analysis-time #inst"2019-03-25T22:45:28.027-00:00",
    :id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
    :config {:file-types [:apex],
             :custom-rules {:apex [snowbird.custom-rules.apex.apex-filename-must-match-pattern
                                   snowbird.custom-rules.apex.no-dml-in-controller]},
             :pmd-rules {:apex "pmdrules_apex_v1.xml"},
             :input {:instruction snowbird.input.files-in-dir,
                     :args {:file-search-path "../tech-debt-measurement"}},
             :render {:instruction snowbird.render.print-tech-debt}},
    :results {:apex {:files-examined ("Class_ServiceLayerUtil_Test.cls"
                                       "Batch_UpdateContacts_Test.cls"
                                       "ChangePasswordController.cls"
                                       "AssetTrigger_Test.cls"
                                       "Model_Asset.cls"
                                       "Scheduled_Batch_GISAddressUpdateClass.cls"
                                       "Model_OrderItem_Test.cls"
                                       "Class_OutcomeException.cls"
                                       "AccountContactTrigger_Test.cls"
                                       "telusComWsdlRmoOrdermgmtEquipmentre.cls"
                                       "TestFactory_Knock.cls"
                                       "Model_WorkflowTemplate_Test.cls"
                                       "LtgController_CSDClaim.cls"
                                       "TriggerHandler_SMS_Test.cls"
                                       "LtgController_OutcomeLiteTest.cls"
                                       "xmlschemaTmiTelusComXsdEnterpriseB.cls"
                                       "CreateCaseFromEmail.cls"
                                       "Batch_RecordsDeleteClass_Test.cls"
                                       "Controller_GeopointeCreateLead.cls"
                                       "Model_Task_Test.cls"
                                       "Model_GenericEventMetadata.cls"
                                       "CommunitiesLoginController.cls"
                                       "RetrieveNextUtils_Test.cls"
                                       "Model_BillingData.cls"
                                       "Controller_DPOBulkUpdate_Test.cls"
                                       "Batch_UpdateContactwithAccountId.cls"
                                       "Controller_DropAccountMemberRole_Test.cls"
                                       "Batch_DeleteOldBillingData_Test.cls"
                                       "Class_RemoveOrphanCustomerAddress_Test.cls"
                                       "MyProfilePageController.cls"
                                       "Class_OutcomeContactUtil.cls"
                                       "Class_SMS_JSON.cls"
                                       "Controller_ContactWorkOrderDisplay.cls"
                                       "Controller_MyProfilePage.cls"),
                     :rules ("ApexBadCrypto"
                              "ApexCSRF"
                              "ApexDangerousMethods"
                              "ApexFilenameMustMatchPattern"
                              "ApexInsecureEndpoint"
                              "ApexOpenRedirect"
                              "ApexSOQLInjection"
                              "ApexSuggestUsingNamedCred"
                              "ApexUnitTestClassShouldHaveAsserts"
                              "ApexUnitTestShouldNotUseSeeAllDataTrue"
                              "ApexXSSFromEscapeFalse"
                              "ApexXSSFromURLParam"
                              "AvoidDmlStatementsInLoops"
                              "AvoidHardcodingId"
                              "AvoidSoqlInLoops"
                              "AvoidSoslInLoops"
                              "CyclomaticComplexity"
                              "EmptyCatchBlock"
                              "EmptyTryOrFinallyBlock"
                              "NoDmlInController"),
                     :violations (
                                   {:line 0,
                                    :description "Lowercase filename 'teluscomwsdlrmoordermgmtequipmentre.cls' does not match any allowed pattern: [#\"trigger\\.cls\" #\"^triggerhandler_\" #\"^controller_\" #\"^controllerext_\" #\"^class_\" #\"^model_\" #\"^batch_\" #\"^scheduled_\" #\"^ltgcontroller_\" #\"^testfactory_\" #\"_test\\.cls\" #\"_longtest\\.cls\"]",
                                    :rule "ApexFilenameMustMatchPattern",
                                    :file-name "telusComWsdlRmoOrdermgmtEquipmentre.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/telusComWsdlRmoOrdermgmtEquipmentre.cls",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 0,
                                    :description "Lowercase filename 'xmlschematmiteluscomxsdenterpriseb.cls' does not match any allowed pattern: [#\"trigger\\.cls\" #\"^triggerhandler_\" #\"^controller_\" #\"^controllerext_\" #\"^class_\" #\"^model_\" #\"^batch_\" #\"^scheduled_\" #\"^ltgcontroller_\" #\"^testfactory_\" #\"_test\\.cls\" #\"_longtest\\.cls\"]",
                                    :rule "ApexFilenameMustMatchPattern",
                                    :file-name "xmlschemaTmiTelusComXsdEnterpriseB.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/xmlschemaTmiTelusComXsdEnterpriseB.cls",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 0,
                                    :description "Lowercase filename 'createcasefromemail.cls' does not match any allowed pattern: [#\"trigger\\.cls\" #\"^triggerhandler_\" #\"^controller_\" #\"^controllerext_\" #\"^class_\" #\"^model_\" #\"^batch_\" #\"^scheduled_\" #\"^ltgcontroller_\" #\"^testfactory_\" #\"_test\\.cls\" #\"_longtest\\.cls\"]",
                                    :rule "ApexFilenameMustMatchPattern",
                                    :file-name "CreateCaseFromEmail.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/CreateCaseFromEmail.cls",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 0,
                                    :description "Lowercase filename 'communitieslogincontroller.cls' does not match any allowed pattern: [#\"trigger\\.cls\" #\"^triggerhandler_\" #\"^controller_\" #\"^controllerext_\" #\"^class_\" #\"^model_\" #\"^batch_\" #\"^scheduled_\" #\"^ltgcontroller_\" #\"^testfactory_\" #\"_test\\.cls\" #\"_longtest\\.cls\"]",
                                    :rule "ApexFilenameMustMatchPattern",
                                    :file-name "CommunitiesLoginController.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/CommunitiesLoginController.cls",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 0,
                                    :description "Lowercase filename 'myprofilepagecontroller.cls' does not match any allowed pattern: [#\"trigger\\.cls\" #\"^triggerhandler_\" #\"^controller_\" #\"^controllerext_\" #\"^class_\" #\"^model_\" #\"^batch_\" #\"^scheduled_\" #\"^ltgcontroller_\" #\"^testfactory_\" #\"_test\\.cls\" #\"_longtest\\.cls\"]",
                                    :rule "ApexFilenameMustMatchPattern",
                                    :file-name "MyProfilePageController.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/MyProfilePageController.cls",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 16,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Scheduled_Batch_GISAddressUpdateClass.cls",
                                    :file-name "Scheduled_Batch_GISAddressUpdateClass.cls",
                                    :rule "NoDmlInController",
                                    :description " private static final String JOB_NAME = 'Address update per GIS load' + (Test.isRunningTest() ? ' TEST' : ''); ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 73,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " insert newClaim; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 76,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update newClaim; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 97,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update newClaim; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 105,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update newClaim; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 112,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " delete obj; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 158,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update lstMaterialUpdate; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 160,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " insert lstMaterialInsert; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 162,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update lstInvoiceUpdate; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 164,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " insert lstInvoiceInsert; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 166,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update lstWitnessUpdate; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 168,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " insert lstWitnessInsert; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 169,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update newClaim; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 182,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update objContent; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 184,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update objAttach; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 192,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update objCliam; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 200,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " delete lstContentDocument; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 230,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " update a; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 239,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/LtgController_CSDClaim.cls",
                                    :file-name "LtgController_CSDClaim.cls",
                                    :rule "NoDmlInController",
                                    :description " insert objAttach; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 157,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/CreateCaseFromEmail.cls",
                                    :file-name "CreateCaseFromEmail.cls",
                                    :rule "NoDmlInController",
                                    :description " insert newCase; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 175,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/CreateCaseFromEmail.cls",
                                    :file-name "CreateCaseFromEmail.cls",
                                    :rule "NoDmlInController",
                                    :description " insert newTask; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 203,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/CreateCaseFromEmail.cls",
                                    :file-name "CreateCaseFromEmail.cls",
                                    :rule "NoDmlInController",
                                    :description " insert newCase; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 180,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_GeopointeCreateLead.cls",
                                    :file-name "Controller_GeopointeCreateLead.cls",
                                    :rule "NoDmlInController",
                                    :description " insert leadData; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 135,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContactwithAccountId.cls",
                                    :file-name "Batch_UpdateContactwithAccountId.cls",
                                    :rule "NoDmlInController",
                                    :description " if(aList.size() > 0) insert aList; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 144,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContactwithAccountId.cls",
                                    :file-name "Batch_UpdateContactwithAccountId.cls",
                                    :rule "NoDmlInController",
                                    :description " update conList; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 152,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContactwithAccountId.cls",
                                    :file-name "Batch_UpdateContactwithAccountId.cls",
                                    :rule "NoDmlInController",
                                    :description " delete aList_tobedeleted_final; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 35,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/MyProfilePageController.cls",
                                    :file-name "MyProfilePageController.cls",
                                    :rule "NoDmlInController",
                                    :description " update user; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 39,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_MyProfilePage.cls",
                                    :file-name "Controller_MyProfilePage.cls",
                                    :rule "NoDmlInController",
                                    :description " update user; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:line 41,
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_MyProfilePage.cls",
                                    :file-name "Controller_MyProfilePage.cls",
                                    :rule "NoDmlInController",
                                    :description " update user.contact; ",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1"}
                                   {:description "Apex unit tests should System.assert() or assertEquals() or assertNotEquals()",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "ApexUnitTestClassShouldHaveAsserts",
                                    :problem "1",
                                    :line "12",
                                    :priority "3",
                                    :file-name "Batch_UpdateContacts_Test.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Batch_UpdateContacts_Test.cls",
                                    :rule-set "Best Practices"}
                                   {:description "Apex unit tests should not use @isTest(seeAllData = true)",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "ApexUnitTestShouldNotUseSeeAllDataTrue",
                                    :problem "1",
                                    :line "14",
                                    :priority "3",
                                    :file-name "Class_ServiceLayerUtil_Test.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_ServiceLayerUtil_Test.cls",
                                    :rule-set "Best Practices"}
                                   {:description "Apex unit tests should not use @isTest(seeAllData = true)",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "ApexUnitTestShouldNotUseSeeAllDataTrue",
                                    :problem "2",
                                    :line "32",
                                    :priority "3",
                                    :file-name "Class_ServiceLayerUtil_Test.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_ServiceLayerUtil_Test.cls",
                                    :rule-set "Best Practices"}
                                   {:description "Apex unit tests should not use @isTest(seeAllData = true)",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "ApexUnitTestShouldNotUseSeeAllDataTrue",
                                    :problem "3",
                                    :line "60",
                                    :priority "3",
                                    :file-name "Class_ServiceLayerUtil_Test.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Class_ServiceLayerUtil_Test.cls",
                                    :rule-set "Best Practices"}
                                   {:description "Avoid untrusted/unescaped variables in DML query",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "ApexSOQLInjection",
                                    :problem "1",
                                    :line "244",
                                    :priority "3",
                                    :file-name "Controller_GeopointeCreateLead.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Controller_GeopointeCreateLead.cls",
                                    :rule-set "Security"}
                                   {:description "Avoid empty catch blocks",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "EmptyCatchBlock",
                                    :problem "1",
                                    :line "11",
                                    :priority "3",
                                    :file-name "Model_GenericEventMetadata.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_GenericEventMetadata.cls",
                                    :rule-set "Error Prone"}
                                   {:description "Avoid empty catch blocks",
                                    :analysis-id #uuid"ba198900-ad9a-4dea-9726-644c81df56f1",
                                    :rule "EmptyCatchBlock",
                                    :problem "2",
                                    :line "26",
                                    :priority "3",
                                    :file-name "Model_GenericEventMetadata.cls",
                                    :file-path "/Users/rgscherf/projects/tech-debt-measurement/classes/Model_GenericEventMetadata.cls",
                                    :rule-set "Error Prone"})}}})



