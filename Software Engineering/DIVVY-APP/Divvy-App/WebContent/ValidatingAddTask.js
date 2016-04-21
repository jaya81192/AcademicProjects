function validateTask(event)
{
	var taskName = document.getElementById("taskName").value;
	if(taskName == "")
	{
		alert("Please enter a task name");
		return false;
	}
	var points = document.getElementById("taskPoints").value;
	if(points == "")
	{
		alert("Please enter points");
		return false;
	}
	if(points<0)
	{
		alert("Enter points between 1 and 10");
		return false;
	}	
	if(points>10)
	{
		alert("Enter points between 1 and 10");
		return false;
	}	
	return true;
}
