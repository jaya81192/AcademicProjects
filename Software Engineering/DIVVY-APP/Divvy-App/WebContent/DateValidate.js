function validateDate(event)
{
	//Read the start date and the end date from the user
	var startDate = new Date();
	startDate = document.getElementById("datepicker").value;
	var endDate = new Date();
	endDate = document.getElementById("datepicker1").value;
	
	//Get the system date
	var today = new Date();
	var dd = today.getDate();
	var mm = today.getMonth()+1; 
	var yyyy = today.getFullYear();

	
	//Get the date and month in the correct format
	if(dd<10) 
	{
		dd='0'+dd;
	} 

	if(mm<10) 
	{
		mm='0'+mm;
	} 

	today = mm+'/'+dd+'/'+yyyy;
	
	//Allow the user to keep the start date as null
	//Program will use system date instead
	if(startDate=" ")
	{
		
	}
	
	//validations for the following
	//Start date should not be before system date
	//End date should not be before start date
	else if(startDate<today)
	{
		alert("Start date is before today");
		return false;
	}
	
	else if(startDate != "" && endDate != "")
	{
		if(endDate<startDate)	
		{
			alert("End date is before start date");
			return false;
		}
	}
	
	return true;
}


//Validation for the advance date feature 
function advanceDateValidate(event)
{
	
	var advanceDate = new Date();
	advanceDate = document.getElementById("datepicker2").value;
	
	//The user cannot leave the new system date null before begnning the testing
	if(advanceDate=" ")
	{
		alert("Please pick a new date");
		
		return false;
	}
	return true;
}
