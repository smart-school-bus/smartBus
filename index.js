'use strict'

const functions = require('firebase-functions');
const admin= require('firebase-admin');
admin.initializeApp(functions.config().firebase);

exports.sendNotification = functions.database.ref('/Emergency/{emergency_id}/{receiver_user_id}/{driver_id}/')
.onWrite((data,context)=>{
	const receiver_user_id=context.params.receiver_user_id;
	const driver_id=context.params.driver_id;
	const emergency_id=context.params.emergency_id;
	const mass=data.after.val().message;
	console.log('we have',emergency_id);
	if(!data.after.val()){
		console.log('we HFHFF',receiver_user_id);
		return null;
	}
	const driver_info=admin.database().ref(`Driver/${driver_id}/`).once('value');
	return driver_info.then(driverResult=>{
		const frist_name=driverResult.val().firstName;
		const last_name=driverResult.val().lastName;
		const DeviceToken=admin.database().ref(`SchoolAdministration/${receiver_user_id}/token`).once('value');
	
 return  DeviceToken.then(result=>{
	const token_id=result.val();
	const payload={
		notification:{
			tag:`${emergency_id}`,
			title:`${frist_name} ${last_name}`,
			body:`${mass}`,
			icon:"default",
			sound:"default",
			click_action:"com.project.smartbus10.EMERGENCY"
		},
		data:{
			idItem:emergency_id,
			ListType:"Emergency"
		}
	};
	return admin.messaging().sendToDevice(token_id,payload)
	.then(response=>{
		console.log("dvcvcv");
	});
});
	});
	
	
});

exports.sendStudentNotification = functions.database.ref('/Student/{student_id}/state')
.onUpdate((change,context)=>{
	const student_id=context.params.student_id;
	const state=change.after.val();
	console.log('check',student_id,state );
	if(!change.after.val()){
		console.log('we HFHFF',student_id,state);
		return null;
	}
	const student_info=admin.database().ref(`Student/${student_id}/`).once('value');
	return student_info.then(sudentResult=>{
		const frist_name=sudentResult.val().firstName;
		const parent_id=sudentResult.val().parentID;
		const bus_id=sudentResult.val().busID;
		const enter_time=sudentResult.val().enterTime;
		const date=sudentResult.val().date;
		const leave_time=sudentResult.val().leaveTime;
		const mass=massge(enter_time,state,leave_time);
	     
			console.log('if',student_id,state );
			const DeviceToken=admin.database().ref(`Parent/${parent_id}/token`).once('value');		
          return  DeviceToken.then(result=>{
			  console.log('parent_id',parent_id );
	      const token_id=result.val();
	      const payload={
		notification:{
			title:`${frist_name}`,
			body:`${mass}`,
			icon:"default",
			sound:"default",
			click_action:"com.project.smartbus10.DETAILS"
		},
		data:{
			idItem:student_id,
			ListType:"Student"
		
		}
	};
	return admin.messaging().sendToDevice(token_id,payload)
	.then(response=>{
		console.log("dvcvcv");
	});
		});
		function massge(enter_time,state, leave_time){
			if(state.localeCompare("on")==0)
			{console.log('if true',state );
				return "Enter the bus "+`${enter_time}`
				}
			console.log('if false',state );
			return"Enter the bus"+`${enter_time}`+" and out "+`${leave_time}`
		
		}
			
	
});});

exports.sendParentNotification = functions.database.ref('/Parent/{parent_id}/latitude/')
.onUpdate((data,context)=>{
	const parent_id=context.params.parent_id;
	console.log('we have',parent_id);
	if(!data.after.val()){
		console.log('we HFHFF',parent_id);
		return null;
	}
	const parent_info=admin.database().ref(`Parent/${parent_id}/`).once('value');
	return parent_info.then(parentResult=>{
		const frist_name=parentResult.val().firstName;
		const last_name=parentResult.val().lastName;
	const DeviceToken=admin.database().ref('SchoolAdministration/A5681/token').once('value');
	
	
 return  DeviceToken.then(result=>{
	const token_id=result.val();
	console.log('we have',result.val());
	const payload={
		notification:{
			title:"Update home address",
			body:`${frist_name} ${last_name}` +"updated the home address",
			icon:"default",
			sound:"default",
			click_action:"com.project.smartbus10.DETAILS"
		},
		data:{
			idItem:parent_id,
			ListType:"Parent"
		
		}
	};
	return admin.messaging().sendToDevice(token_id,payload)
	.then(response=>{
		console.log("dvcvcv");
	});
});
	});
	
});