// Function to create and display the iframe
function loadInFrame(url) {
  // Check if the iframe container already exists, if not create it
  let iframeContainer = document.getElementById('iframeContainer');

  // If the container doesn't exist, create it dynamically
  if (!iframeContainer) {
    // Create overlay background
    const overlay = document.createElement('div');
    overlay.id = 'iframeOverlay';
    overlay.style.position = 'fixed';
    overlay.style.top = '0';
    overlay.style.left = '0';
    overlay.style.width = '100%';
    overlay.style.height = '100%';
    overlay.style.backgroundColor = 'rgba(0, 0, 0, 0.7)';
    overlay.style.zIndex = '999';
    overlay.style.transition = 'opacity 0.3s ease';
    document.body.appendChild(overlay);

    // Create the iframe container
    iframeContainer = document.createElement('div');
    iframeContainer.id = 'iframeContainer';
    iframeContainer.style.position = 'fixed';
    iframeContainer.style.top = '50%';
    iframeContainer.style.left = '50%';
    iframeContainer.style.transform = 'translate(-50%, -50%)';
    iframeContainer.style.width = '70%';
    iframeContainer.style.maxWidth = '600px';
    iframeContainer.style.height = '400px';
    iframeContainer.style.backgroundColor = '#fff';
    iframeContainer.style.borderRadius = '16px';
    iframeContainer.style.padding = '20px';
    iframeContainer.style.boxShadow = '0 10px 30px rgba(0, 0, 0, 0.3)';
    iframeContainer.style.overflow = 'hidden';
    iframeContainer.style.zIndex = '1000';
    iframeContainer.style.transition = 'all 0.3s ease';
    iframeContainer.style.opacity = '0'; // Initial state for transition
    iframeContainer.style.display = 'flex';
    iframeContainer.style.flexDirection = 'column';

    // Create the header for styling
    const header = document.createElement('div');
    header.id = 'iframeHeader';
    header.style.backgroundColor = '#00b2ff';
    header.style.color = '#fff';
    header.style.padding = '12px 20px';
    header.style.borderTopLeftRadius = '16px';
    header.style.borderTopRightRadius = '16px';
    header.style.marginBottom = '5px';
    header.style.display = 'flex';
    header.style.justifyContent = 'space-between';
    header.style.alignItems = 'center';

    // Create and configure the title
    const title = document.createElement('span');
    title.textContent = 'Member Details';
    title.style.fontSize = '18px';
    title.style.fontWeight = 'bold';
    title.style.flexGrow = '1';
    title.style.textAlign = 'left';
    header.appendChild(title);

    // Create and configure the close button
    const closeButton = document.createElement('button');
    closeButton.textContent = '✕';
    closeButton.style.backgroundColor = '#d9534f';
    closeButton.style.color = '#fff';
    closeButton.style.border = 'none';
    closeButton.style.padding = '8px 12px';
    closeButton.style.cursor = 'pointer';
    closeButton.style.borderRadius = '50%';
    closeButton.style.fontSize = '18px';
    closeButton.style.transition = 'background-color 0.3s ease';
    closeButton.style.marginLeft = '20px';

    // Add hover effect to the close button
    closeButton.onmouseover = function () {
      closeButton.style.backgroundColor = '#c9302c';
    };
    closeButton.onmouseout = function () {
      closeButton.style.backgroundColor = '#d9534f';
    };

    // Add click event to the close button to remove the iframe container and overlay
    closeButton.onclick = function () {
      iframeContainer.style.opacity = '0';
      overlay.style.opacity = '0';
      setTimeout(() => {
        document.getElementById('iframeContainer').remove();
        document.getElementById('iframeOverlay').remove();
      }, 300);
    };

    // Append close button to header
    header.appendChild(closeButton);

    // Create and configure the iframe
    const iframe = document.createElement('iframe');
    iframe.id = 'popupFrame';
    iframe.width = '100%';
    iframe.height = '100%';
    iframe.style.border = 'none';
    iframe.src = url; // Set the URL of the iframe to the clicked link

    // Append the header and the iframe to the container
    iframeContainer.appendChild(header);
    iframeContainer.appendChild(iframe);

    // Append the container to the body
    document.body.appendChild(iframeContainer);

    // Set opacity for smooth transition effect
    setTimeout(() => {
      iframeContainer.style.opacity = '1';
      overlay.style.opacity = '1';
    }, 10);
  } else {
    // If the container already exists, just change the URL of the iframe
    document.getElementById('popupFrame').src = url;
  }

  return false;
}
