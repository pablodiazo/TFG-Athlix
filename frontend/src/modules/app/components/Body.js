import React from "react";

import { Route, Routes } from "react-router-dom";

import Home from "./Home";
import Test from "./Test";

import { Login, Profile, SignUp, ChangePassword, UpdateProfile , ManageAthletes, PendingRequests } from "../../users";
import { DailyPlan, WeeklyPlan, MonthlyPlan, CreateTrainingSession, CreateSessionSuccess, CreateNutritionPlan, CreateRestPlan, CoachDashboard ,
   RescheduleRequests, EditTrainingSession , EditNutritionPlan, EditRestPlan } from "../../plans";

const Body = () => {
  return (
    <Routes>
      <Route path="/">
        <Route index exact element={<Home />} />
        <Route path="/test" element={<Test />} />
        <Route path="/users/login" element={<Login />} />
        <Route path="/users/profile" element={<Profile />} />
        <Route path="/users/signup" element={<SignUp />} />
        <Route path="/users/changePassword" element={<ChangePassword/>}/>
        <Route path="/users/updateProfile" element={<UpdateProfile/>}/>
        <Route path="/plans/daily" element={<DailyPlan />} />
        <Route path="/plans/weekly" element={<WeeklyPlan />} />
        <Route path="/plans/monthly" element={<MonthlyPlan />} />
        <Route path="/plans/create-training-session" element={<CreateTrainingSession />} />
        <Route path="/plans/create-session-success" element={<CreateSessionSuccess />} />
        <Route path="/plans/create-nutrition-plan" element={<CreateNutritionPlan />} />
        <Route path="/plans/create-rest-plan" element={<CreateRestPlan />} />
        <Route path="/plans/athletes" element={<CoachDashboard />} />
        <Route path="/plans/reschedule-requests" element={<RescheduleRequests/>}/>
        <Route path="/plans/edit-session/:id" element={<EditTrainingSession />} />
        <Route path="/plans/edit-nutrition-plan/:id" element={<EditNutritionPlan />} />
        <Route path="/plans/edit-rest-plan/:id" element={<EditRestPlan />} />
        <Route path="/users/manage-athletes" element={<ManageAthletes />} />
        <Route path="/users/coach-requests" element={<PendingRequests />} />
      </Route>
    </Routes>
  );
};

export default Body;
